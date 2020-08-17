package com.designwright.multithreadchat.server2.loader;

import com.designwright.multithreadchat.server2.exception.InternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Slf4j
public class ApplicationLoader extends AbstractLoader {

    private final Class<?> applicationEntry;
    private final ApplicationContext context;
    private final Set<Class<? extends Annotation>> annotationScanExcludes;

    public ApplicationLoader(Class<?> applicationEntry) {
        this.applicationEntry = applicationEntry;
        context = new ApplicationContext();
        annotationScanExcludes = new HashSet<>(
                Arrays.asList(
                        Inherited.class,
                        Documented.class,
                        Retention.class,
                        Target.class
                )
        );
    }

    public void start(String[] args) {
        try {
            File root = loadClassDirectory();
            List<File> files = directoryScan(root);
            Set<ClassResource> resources = loadResources(files);
            initializeEntryPoint(resources);
        } catch (Exception e) {
            log.error("Uncaught exception", e);
        }
    }

    Set<ClassResource> loadResources(List<File> files) {
        Set<ClassResource> resources = new HashSet<>();
        String domainLocation = getDomainLocation();

        for (File file : files) {
            try {
                ClassResource resource = new ClassResource(file.getPath(), domainLocation);
                resource.parseData();
                resources.add(resource);
            } catch (MalformedURLException e) {
                throw new InternalServiceException("Could not load class resource", e);
            }
        }

        return uniqueResources(resources);
    }

    Set<ClassResource> uniqueResources(Set<ClassResource> resources) {
        Set<ClassResource> uniqueResources = new HashSet<>();

        for (ClassResource resource : resources) {
            if (uniqueResources.stream().noneMatch(r -> resource.getCanonicalName().equals(r.getCanonicalName()))) {
                uniqueResources.add(resource);
            }
        }

        return uniqueResources;
    }

    void initializeEntryPoint(Set<ClassResource> resources) {
        Class<?> entryClass = applicationEntry;
        ApplicationStart annotation = entryClass.getAnnotation(ApplicationStart.class);

        if (annotation != null) {
            String packageScan;

            if (StringUtils.isEmpty(annotation.packageName())) {
                packageScan = entryClass.getPackage().getName();
            } else {
                packageScan = annotation.packageName();
            }

            scanBeans(resources, packageScan);
        } else {
            throw new InternalServiceException("Entry point must be annotated with ApplicationStart");
        }
    }

    void scanBeans(Set<ClassResource> resources, String packageName) {
        for (ClassResource resource : resources) {
            if (resource.getCanonicalName().startsWith(packageName)) {
                try {
                    Class<?> beanClass = Class.forName(resource.getCanonicalName());

                    if (isComponent(beanClass)) {
                        createBean(beanClass);
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Class not found on classpath: " + resource.getCanonicalName(), e);
                }
            }
        }

        this.context.beanPostProcessor();
    }

    boolean isComponent(Class<?> classType) {
        return !classType.isAnnotation() && hasAnnotation(classType, Component.class);
    }

    boolean hasAnnotation(Class<?> classType, Class<? extends Annotation> findAnnotation) {
        boolean hasAnnotation = false;
        Iterator<Annotation> iterator = Arrays.asList(classType.getAnnotations()).iterator();

        while (!hasAnnotation && iterator.hasNext()) {
            Annotation annotation = iterator.next();

            if (!annotationScanExcludes.contains(annotation.annotationType())) {
                if (annotation.annotationType().equals(findAnnotation)) {
                    hasAnnotation = true;
                } else {
                    hasAnnotation = hasAnnotation(annotation.annotationType(), findAnnotation);
                }
            }
        }

        return hasAnnotation;
    }

    void createBean(Class<?> beanClass) {
        String beanName = StringUtils.uncapitalize(beanClass.getSimpleName());
        Annotation[] annotations = beanClass.getAnnotations();
        ApplicationBean bean = new ApplicationBean();
        bean.setType(beanClass);
        boolean isConfiguration = false;

        //TODO: Clean this up
        for (Annotation annotation : annotations) {
            if (annotation instanceof Component) {
                String name = ((Component) annotation).beanName();
                if (!StringUtils.isEmpty(name)) {
                    beanName = name;
                }
            } else if (annotation instanceof Service) {
                String name = ((Service) annotation).beanName();
                if (!StringUtils.isEmpty(name)) {
                    beanName = name;
                }
            } else if (annotation instanceof Configuration) {
                isConfiguration = true;
            }
            bean.addAnnotation(annotation);
        }
        bean.setName(beanName);

        if (isConfiguration) {
            processConfigurationBeans(bean);
        } else {
            processBean(bean);
        }
    }

    void processConfigurationBeans(ApplicationBean bean) {
        for (Method method : bean.getType().getDeclaredMethods()) {
            processConfigurationBean(bean, method);
        }
        processBean(bean);
    }

    void processConfigurationBean(ApplicationBean parentBean, Method method) {
        Bean annotation = method.getAnnotation(Bean.class);

        if (annotation != null) {
            ApplicationBean bean = new ApplicationBean();
            bean.setMethod(true);
            bean.setConstructor(method);
            bean.setType(method.getReturnType());
            bean.setDependsOn(parentBean);

            if (StringUtils.isEmpty(annotation.beanName())) {
                bean.setName(method.getName());
            } else {
                bean.setName(annotation.beanName());
            }

            Parameter[] parameters = method.getParameters();

            if (parameters.length > 0) {
                for (Parameter parameter : parameters) {
                    addBeanDependency(bean, parameter);
                }
            }

            this.context.addBean(bean);
        }
    }

    void processBean(ApplicationBean bean) {
        if (!context.hasBean(bean.getName())) {
            processBeanConstructor(bean);
            processBeanPostConstructor(bean);
            this.context.addBean(bean);
        } else {
            log.error("Bean " + bean.getName() + " has already been instantiated, try using a different name.");
        }
    }

    void processBeanConstructor(ApplicationBean bean) {
        Constructor<?>[] constructors = bean.getType().getConstructors();

        if (constructors.length == 1) {
            bean.setConstructor(constructors[0]);
            addBeanDependencies(bean);
        } else {
            log.error("Could not create bean of type " + bean.getType().getCanonicalName() + ", beans must have only one constructor");
        }
    }

    void processBeanPostConstructor(ApplicationBean bean) {
        Method[] methods = bean.getType().getMethods();

        for (Method method : methods) {
            PostConstruct annotation = method.getAnnotation(PostConstruct.class);

            if (annotation != null) {
                if (bean.getPostConstruct() == null) {
                    bean.setPostConstruct(method);
                } else {
                    throw new IllegalStateException("Multiple post construct methods not allowed");
                }
            }
        }
    }

    void addBeanDependencies(ApplicationBean bean) {
        Parameter[] parameters = bean.getConstructor().getParameters();

        if (parameters.length > 0) {
            for (Parameter parameter : parameters) {
                addBeanDependency(bean, parameter);
            }
        }
    }

    void addBeanDependency(ApplicationBean bean, Parameter parameter) {
        bean.addDependency(parameter.getParameterizedType());
    }

}
