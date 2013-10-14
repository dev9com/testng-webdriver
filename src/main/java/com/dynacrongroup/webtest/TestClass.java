package com.dynacrongroup.webtest;

import org.testng.ITestNGMethod;

import java.lang.reflect.Field;
import java.util.*;

public final class TestClass {
    private Field webDriverField;
    private Class driverAnnotation;
    private Class testClass;
    private boolean enabled = true;
    private boolean webDriverRunning = false;
    private List<String> excludedMethods = new ArrayList<String>();
    private Object classInstance;
    private List<ITestNGMethod> invokedMethods = new ArrayList<ITestNGMethod>();

    public TestClass setWebDriverField(Field webDriverField) {
        this.webDriverField = webDriverField;
        return this;
    }

    public TestClass setDriverAnnotation(Class driverAnnotation) {
        this.driverAnnotation = driverAnnotation;
        return this;
    }

    public TestClass setTestClass(Class testClass) {
        this.testClass = testClass;
        return this;
    }

    public TestClass setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public TestClass setExcludedMethods(String[] excludedMethods) {
        this.excludedMethods = Arrays.asList(excludedMethods);
        return this;
    }

    public TestClass setClassInstance(Object classInstance) {
        this.classInstance = classInstance;
        return this;
    }

    public TestClass addInvokedMethod(ITestNGMethod method) {
        invokedMethods.add(method);
        return this;
    }

    public TestClass setWebDriverRunning(boolean running) {
        this.webDriverRunning = running;
        return this;
    }

    public Field getWebDriverField() {
        return webDriverField;
    }

    public Class getDriverAnnotation() {
        return driverAnnotation;
    }

    public Class getTestClass() {
        return testClass;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getExcludedMethods() {
        return excludedMethods;
    }

    public boolean isWebDriverRunning() {
        return webDriverRunning;
    }

    public Object getClassInstance() {
        return classInstance;
    }

    public List<ITestNGMethod> getInvokedMethods() {
        return invokedMethods;
    }
}
