package com.dynacrongroup.webtest.reporters;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.annotation.MethodDriver;
import com.dynacrongroup.webtest.driver.ThreadLocalWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class SeleniumWebDriver extends TestListenerAdapter {

    /**
     * Used to maintain the drivers state. If true the driver will not start before
     * the next test. Default = false
     */
    private ThreadLocal<Boolean> isDriverRunning = new ThreadLocal<Boolean>() {{
        set(false);
    }};

    /**
     * Used to track if the user supplied @ClassDriver annotation
     */
    private ThreadLocal<Boolean> classDriver = new ThreadLocal<Boolean>() {{
        set(true);
    }};

    /**
     * Used to track if this listener has completed it's first run
     */
    private ThreadLocal<Boolean> isFirstPass = new ThreadLocal<Boolean>() {{
        set(true);
    }};

    /**
     * Stores the Field which is the WebDriver variable in the test class
     */
    private ThreadLocal<Field> webDriverField = new ThreadLocal<Field>();

    /**
     * Stores the test class Object
     */
    private ThreadLocal<Object> testObject = new ThreadLocal<Object>();

    /**
     * Keeps track of if an exception was thrown in this class. If this is set to true System.quit is called.
     */
    private ThreadLocal<Boolean> isError = new ThreadLocal<Boolean>(){{set(false);}};

    /**
     * Holds the list of methods the user passed in that should not have a driver initialized.
     */
    private ThreadLocal<List<String>> excludedMethods = new ThreadLocal<List<String>>();

    /**
     * String name of the current test method.
     */
    private ThreadLocal<String> currentMethod = new ThreadLocal<String>();

    /**
     * Holds the boolean the user passed in which disables initializing a WebDriver for all tests.
     */
    private ThreadLocal<Boolean> isWebDriverEnabled = new ThreadLocal<Boolean>(){{set(false);}};

    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart(result);

        // If the listener threw an error DO NOT run any tests
        if (isError.get()) {
            System.exit(0);
        }

        if (isWebDriverEnabled.get()) {
            return;
        }

        currentMethod.set(result.getMethod().getMethodName());

        // On first pass find the annotation and initialize the WebDriver
        if (isFirstPass.get()) {
            testObject.set(result.getInstance());
            isFirstPass.set(false);
            initializeWebDriver();
        } else if (!isDriverRunning.get()) {
            setMethodDriver();
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        updateWebDriver();
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        updateWebDriver();
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        updateWebDriver();
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        killDriver();
    }

    private void initializeWebDriver() {
        Class annotation = getAnnotation();
        if (isWebDriverEnabled.get()) {
            if (annotation.equals(ClassDriver.class)) {
                setClassDriver();
            } else if (annotation.equals(MethodDriver.class)) {
                setMethodDriver();
            }
        }
    }

    private Class getAnnotation() {
        Field[] fields = testObject.get().getClass().getFields();
        boolean classDriver = false;
        boolean methodDriver = false;

        for (Field field : fields) {
            if (field.isAnnotationPresent(ClassDriver.class)) {
                classDriver = true;
                webDriverField.set(field);
                setClassDriverParams();
            } else if (field.isAnnotationPresent(MethodDriver.class)) {
                methodDriver = true;
                webDriverField.set(field);
                setMethodDriverParams();
            }
        }

        if (classDriver && methodDriver) {
            isError.set(true);
            throw new IllegalStateException(
                    "Found @ClassDriver and @MethodDriver in test class. This is currently unsupported.");
        } else if (classDriver) {
            return ClassDriver.class;
        } else if (methodDriver) {
            return MethodDriver.class;
        }

        return null;
    }

    private void setClassDriver() {
        try {
            if (webDriverField.get().getType().equals(WebDriver.class)) {
                isDriverRunning.set(true);
                webDriverField.get().set(testObject.get(), new ThreadLocalWebDriver(this.getClass()));
            } else {
                throw new IllegalArgumentException("@ClassDriver must be set on a WebDriver field");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setMethodDriver() {
        try {
            if (webDriverField.get().getType().equals(WebDriver.class)) {
                isDriverRunning.set(true);
                classDriver.set(false);
                if (!excludedMethods.get().contains(currentMethod.get())) {
                    webDriverField.get().set(testObject.get(), new ThreadLocalWebDriver(this.getClass()));
                } else {
                    webDriverField.get().set(testObject.get(), null);
                }
            } else {
                throw new IllegalArgumentException("@MethodDriver must be set on a WebDriver field");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setMethodDriverParams() {
        MethodDriver annotation = webDriverField.get().getAnnotation(MethodDriver.class);
        excludedMethods.set(Arrays.asList(annotation.excludeMethods()));
        isWebDriverEnabled.set(annotation.enabled());
    }

    private void setClassDriverParams() {
        ClassDriver annotation = webDriverField.get().getAnnotation(ClassDriver.class);
        isWebDriverEnabled.set(annotation.enabled());
    }

    private void updateWebDriver() {
        if (classDriver.get().equals(false)) {
            killDriver();
            isDriverRunning.set(false);
        }
    }

    private void killDriver() {
        try {
            Field field = webDriverField.get();
            WebDriver driver = (WebDriver) field.get(testObject.get());
            driver.quit();
        } catch (Exception e) {
            /* Ignore driver exception on close */
        }
    }
}