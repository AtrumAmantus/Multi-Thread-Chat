package com.designwright.core.container;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
public class ApplicationBean {

    private String name;
    private Class<?> type;
    private Object instance;
    private List<Annotation> annotations;
    private List<Type> dependencies;
    private Executable constructor;
    private ApplicationBean dependsOn;
    private Method postConstruct;
    private boolean method;

    public ApplicationBean() {
        annotations = new ArrayList<>();
        dependencies = new ArrayList<>();
    }

    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    public void addDependency(Type classType) {
        dependencies.add(classType);
    }

    public boolean isInstantiated() {
        return instance != null;
    }

}
