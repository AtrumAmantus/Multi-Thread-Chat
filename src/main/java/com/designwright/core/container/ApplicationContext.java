package com.designwright.core.container;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ApplicationContext {

    private final Map<String, ApplicationBean> beans;
    private final Set<ApplicationBean> processingBeans;

    public ApplicationContext() {
        beans = new HashMap<>();
        processingBeans = new HashSet<>();
    }

    public void addBean(ApplicationBean applicationBean) {
        beans.put(applicationBean.getName(), applicationBean);
    }

    public ApplicationBean getBean(String name) {
        return beans.get(name);
    }

    public boolean hasBean(String name) {
        return beans.containsKey(name);
    }

    public void beanPostProcessor() {
        log.info(beans.size() + " beans loaded.");
        List<ApplicationBean> beanList = new ArrayList<>(this.beans.values());
        postProcessBeans(beanList);
        initializeBeans(beanList);
    }

    void postProcessBeans(List<ApplicationBean> beans) {
        for (ApplicationBean bean : beans) {
            postProcessBean(bean);
        }
    }

    void initializeBeans(List<ApplicationBean> beans) {
        for (ApplicationBean bean : beans) {
            initializeBean(bean);
        }
    }

    void initializeBean(ApplicationBean bean) {
        if (bean.getPostConstruct() != null) {
            try {
                bean.getPostConstruct().invoke(bean.getInstance());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Could not initialize bean " + bean.getType() + "'s post construct method.");
            }
        }
    }

    void postProcessBean(ApplicationBean bean) {
        if (!processingBeans.contains(bean)) {
            if (!bean.isInstantiated()) {
                processingBeans.add(bean);
                if (bean.getDependencies().isEmpty()) {
                    instantiateBean(bean);
                } else {
                    processBeanDependencies(bean);
                }
                processingBeans.remove(bean);
            }
        } else {
            throw new IllegalStateException("Bean is already processing: " + bean.getName());
        }
    }

    void processBeanDependencies(ApplicationBean bean) {
        List<Object> dependencyBeans = new ArrayList<>();

        for (Type dependency : bean.getDependencies()) {
            try {
                if (dependency instanceof Class) {
                    dependencyBeans.add(
                            processBeanClassDependency((Class<?>) dependency)
                    );
                } else if (dependency instanceof ParameterizedType) {
                    dependencyBeans.add(
                            processBeanParameterizedTypeDependency((ParameterizedType) dependency)
                    );
                } else {
                    throw new IllegalStateException("Unsupported bean dependency type '" + dependency.getTypeName() + "' from bean " + bean.getName());
                }
            } catch (MalformedParameterizedTypeException e) {
                throw new IllegalArgumentException("Invalid parameterized type '" + dependency.getTypeName() + "' for bean " + bean.getName());
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Could not instantiate bean " + bean.getName(), e);
            }
        }
        instantiateBean(bean, dependencyBeans.toArray());
    }

    Object processBeanClassDependency(Class<?> dependency) {
        ApplicationBean dependencyBean;
        List<ApplicationBean> assignableBeans = getApplicationBeansOfType(dependency);

        if (assignableBeans.size() == 1) {
            dependencyBean = assignableBeans.get(0);

            if (!dependencyBean.isInstantiated()) {
                postProcessBean(dependencyBean);
            }
        } else if (assignableBeans.isEmpty()) {
            throw new IllegalStateException("No valid bean definitions for constructor argument '" + dependency.getCanonicalName() + "'");
        } else {
            throw new IllegalStateException("Multiple bean definitions for constructor argument '" + dependency.getCanonicalName() + "'");
        }
        return dependencyBean.getInstance();
    }

    Object processBeanParameterizedTypeDependency(ParameterizedType paramType) {
        Object beanInstances;
        if (((Class<?>) paramType.getRawType()).isAssignableFrom(List.class)) {
            beanInstances = processBeanListTypeDependency(paramType);
        } else {
            beanInstances = processBeanGenericParameterizedTypeDependency(paramType);
        }
        return beanInstances;
    }

    List<Object> processBeanListTypeDependency(ParameterizedType paramType) {
        List<Object> beanInstances = new ArrayList<>();
        Type[] paramTypeTypes = paramType.getActualTypeArguments();
        Type paramTypeType = paramTypeTypes[0];

        if (paramTypeType instanceof Class) {
            Class<?> paramTypeClass = ((Class<?>) paramTypeType);
            List<ApplicationBean> assignableBeans = getApplicationBeansOfType(paramTypeClass);

            for (ApplicationBean bean : assignableBeans) {
                if (!bean.isInstantiated()) {
                    postProcessBean(bean);
                }
            }

            assignableBeans.forEach(bean -> beanInstances.add(bean.getInstance()));
        } else {
            throw new MalformedParameterizedTypeException();
        }

        return beanInstances;
    }

    Object processBeanGenericParameterizedTypeDependency(ParameterizedType paramType) {
        Object beanInstance;
        List<ApplicationBean> assignableBeans = getApplicationBeansOfType((Class<?>) paramType.getRawType());

        if (assignableBeans.size() == 1) {
            ApplicationBean bean = assignableBeans.get(0);

            //TODO: Handle multiple interfaces
            Type genericInterface = bean.getType().getGenericInterfaces()[0];

            if (genericInterface instanceof ParameterizedType) {
                if (
                        Arrays.equals(
                                ((ParameterizedType) genericInterface).getActualTypeArguments(),
                                paramType.getActualTypeArguments()
                        )
                ) {
                    if (!bean.isInstantiated()) {
                        postProcessBean(bean);
                    }

                    beanInstance = bean.getInstance();
                } else {
                    //TODO: Correctly handle multiple parameterized type parameters
                    throw new IllegalStateException("FIXME Invalid parameterized type");
                }
            } else {
                throw new IllegalStateException("FIXME Interface is no parameterized type");
            }
        } else if (assignableBeans.isEmpty()) {
            throw new IllegalStateException("FIXME No bean definitions");
        } else {
            throw new IllegalStateException("FIXME Too many bean definitions");
        }

        return beanInstance;
    }

    public <T> List<T> getBeansOfType(Class <T> classType) {
        return getApplicationBeansOfType(classType).stream()
                .map(b -> (T)b.getInstance())
                .collect(Collectors.toList());
    }

    private List<ApplicationBean> getApplicationBeansOfType(Class<?> classType) {
        return beans.values().stream()
                .filter(b -> classType.isAssignableFrom(b.getType()))
                .collect(Collectors.toList());
    }

    void instantiateBean(ApplicationBean bean) {
        instantiateBean(bean, null);
    }

    void instantiateBean(ApplicationBean bean, Object[] dependencies) {
        try {
            if (bean.isMethod()) {
                if (!bean.getDependsOn().isInstantiated()) {
                    postProcessBean(bean.getDependsOn());
                }

                bean.getConstructor().setAccessible(true);
                bean.setInstance(((Method) bean.getConstructor()).invoke(bean.getDependsOn().getInstance(), dependencies));
            } else {
                bean.setInstance(((Constructor<?>) bean.getConstructor()).newInstance(dependencies));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not create bean: " + bean.getName(), e);
        }
    }

}
