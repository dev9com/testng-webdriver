package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.annotation.MethodDriver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestNGMethod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TestClass {
    private Field webDriverField;
    private Class driverAnnotation;
    private Class testClass;
    private boolean enabled = true;
    private List<String> excludedMethods = new ArrayList<String>();
    private Object classInstance = new Object();
    private Map<String, Integer> testMethods = new HashMap<String, Integer>();

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

    public TestClass addTestMethod(ITestNGMethod method) {
        int count = (testMethods.get(method.getMethodName()) == null) ? 0 : testMethods.get(method.getMethodName());

        if (count == 0) {
            testMethods.put(method.getMethodName(), 1);
        } else {
            testMethods.put(method.getMethodName(), count + 1);
        }
        return this;
    }

    public TestClass removeTestMethod(ITestNGMethod method) {
        int count = (testMethods.get(method.getMethodName()) == null) ? -1 : testMethods.get(method.getMethodName());

        // If we only have one left remove the method as completed
        if (count == 1) {
            testMethods.remove(method.getMethodName());
        // If we have more then one drop count by one
        } else if (count > 1){
            testMethods.put(method.getMethodName(), count - 1);
        }
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
        try {
            return getWebDriverField().get(getClassInstance()) instanceof WebDriver;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return true;
    }

    public Object getClassInstance() {
        return classInstance;
    }

    public int methodsLeftInClass() {
        return testMethods.size();
    }

    public boolean isMethodDriver() {
        return driverAnnotation.equals(MethodDriver.class);
    }

    public boolean isClassDriver() {
        return driverAnnotation.equals(ClassDriver.class);
    }
}
