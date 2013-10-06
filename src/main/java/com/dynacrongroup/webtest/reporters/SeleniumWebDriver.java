package com.dynacrongroup.webtest.reporters;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.annotation.MethodDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.lang.reflect.Field;

public class SeleniumWebDriver extends TestListenerAdapter {

    private ThreadLocal<Boolean> hasDriverStarted = new ThreadLocal<Boolean>() {{
        set(false);
    }};
    private ThreadLocal<Boolean> classDriver = new ThreadLocal<Boolean>() {{
        set(true);
    }};
    private ThreadLocal<Field> testWebDriver = new ThreadLocal<Field>();
    private ThreadLocal<Boolean> checkedForFields = new ThreadLocal<Boolean>(){{set(false);}};
    private ThreadLocal<Object> testObject = new ThreadLocal<Object>();

    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart(result);

        testObject.set(result.getInstance());
        if (checkedForFields.get().equals(false)) {
            checkedForFields.set(true);
            setWebDriverFirstTime();
        } else if (hasDriverStarted.get().equals(false)) {
            setWebDriverStartOfMethod();
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        endOfTestUpdateMethodDriver();
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        endOfTestUpdateMethodDriver();
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        endOfTestUpdateMethodDriver();
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        killDriver();
    }

    private void setWebDriverFirstTime() {
        Field[] fields = testObject.get().getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ClassDriver.class)) {
                setClassDriver(field);
                return;
            } else if (field.isAnnotationPresent(MethodDriver.class)) {
                setMethodDriver(field);
                return;
            }
        }
    }

    private void setClassDriver(Field field) {
        try {
            if (field.getType().equals(WebDriver.class)) {
                hasDriverStarted.set(true);
                field.set(testObject.get(), new FirefoxDriver());
                testWebDriver.set(field);
            } else {
                throw new IllegalArgumentException("@ClassDriver must be set on a WebDriver field");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setMethodDriver(Field field) {
        try {
            if (field.getType().equals(WebDriver.class)) {
                hasDriverStarted.set(true);
                classDriver.set(false);
                field.set(testObject.get(), new FirefoxDriver());
                testWebDriver.set(field);
            } else {
                throw new IllegalArgumentException("@MethodDriver must be set on a WebDriver field");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setWebDriverStartOfMethod() {
        setMethodDriver(testWebDriver.get());
    }

    private void endOfTestUpdateMethodDriver() {
        if (classDriver.get().equals(false)) {
            killDriver();
            hasDriverStarted.set(false);
        }
    }

    private void killDriver() {
        try {
            Field field = testWebDriver.get();
            WebDriver driver = (WebDriver) field.get(testObject.get());
            driver.quit();
        } catch (Exception e) {
            /* Ignore driver exception on close */
            e.printStackTrace();
        }
    }
}
