package com.dynacrongroup.webtest.listeners;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.annotation.MethodDriver;
import com.dynacrongroup.webtest.driver.ThreadLocalWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.lang.reflect.Field;
import java.util.*;

/**
 * The purpose of this class is to start a new WebDriver instance on an annotated WebDriver variable.
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 10/1/13
 */
public class SeleniumWebDriver extends TestListenerAdapter {

    private static Map<Class, List<ITestNGMethod>> classListMap = new HashMap<Class, List<ITestNGMethod>>();
    ThreadLocal<Field> webDriverField = new ThreadLocal<Field>();
    // Keeps track if Driver annotation exists and if driver is enabled
    ThreadLocal<Boolean> isDriverTest = new ThreadLocal<Boolean>() {{
        set(false);
    }};
    ThreadLocal<List<String>> excludedMethods = new ThreadLocal<List<String>>();
    ThreadLocal<String> testDescription = new ThreadLocal<String>();
    ThreadLocal<Object> testClassInstance = new ThreadLocal<Object>();

    /*==========================================================================
                                         Start
      ==========================================================================*/

    @Override
    public void onStart(ITestContext tc) {
        super.onStart(tc);
        setClassListMap(tc);
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        setWebDriverField(tr);
        setTestDescription(tr);
        setTestClassInstance(tr);
        if (isDriverTest.get()) {
            setIsTestDisabled(webDriverField.get());
            setExcludedMethods(webDriverField.get());
            startDriver(tr);
        }
    }

    /*==========================================================================
                                         Finish
      ==========================================================================*/

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        endDriver(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        endDriver(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        endDriver(tr);
    }

    /*==========================================================================
                                      Class Methods
      ==========================================================================*/

    private void startDriver(ITestResult tr) {
        if (isDriverTest.get()) {
            if (!isTestExcluded(tr)) {
                if (!isDriverRunning()) {
                    initializeDriver(tr);
                }
            } else {
                setDriverNull();
            }
        }
    }

    private void initializeDriver(ITestResult tr) {
        try {
            webDriverField.get().set(
                    testClassInstance.get(), new ThreadLocalWebDriver(getRealTestClass(tr), testDescription.get()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void endDriver(ITestResult tr) {
        if (isClassDriver()) {
            ITestNGMethod current = tr.getMethod();
            Class currentClass = current.getRealClass();
            List<ITestNGMethod> remainingMethods = classListMap.get(currentClass);
            ITestNGMethod foundMethod = null;
            for (ITestNGMethod m : remainingMethods) {
                if (m.getMethodName().equals(current.getMethodName())) {
                    foundMethod = m;
                    int count = m.getInvocationCount();
                    if (count > 1) {
                        m.setInvocationCount(count - 1);
                        return;
                    }
                }
            }

            if (foundMethod != null) {
                remainingMethods.remove(foundMethod);
            }

            if (remainingMethods.size() == 0) {
                quitDriver();
            }
        } else {
            quitDriver();
        }
    }

    private void quitDriver() {
        try {
            WebDriver driver = (WebDriver) webDriverField.get().get(testClassInstance.get());
            driver.quit();
        } catch (Exception e) {/* Ignore if driver has already quit */ }
    }

    private void setDriverNull() {
        try {
            webDriverField.get().set(testClassInstance.get(), null);
        } catch (Exception e) {/* Ignore error */ }
    }

    private boolean isTestExcluded(ITestResult tr) {
        return excludedMethods.get() != null && excludedMethods.get().contains(tr.getMethod().getMethodName());
    }

    private boolean isDriverRunning() {
        Field webDriver = webDriverField.get();
        if (webDriver == null) return false;

        // Horrid way to check if .quit() was called on the driver.
        try {
            WebDriver driver = (WebDriver) webDriver.get(testClassInstance.get());
            driver.getTitle();
            return true;
        } catch (Exception e) { return false; }
    }

    private Class getRealTestClass(ITestResult tr) {
        return tr.getTestClass().getRealClass();
    }

    private void setWebDriverField(ITestResult tr) {
        Class testClass = getRealTestClass(tr);
        for (Field classField : testClass.getFields()) {
            if (classField.isAnnotationPresent(ClassDriver.class)) {
                webDriverField.set(classField);
                isDriverTest.set(true);
                return;
            } else if (classField.isAnnotationPresent(MethodDriver.class)) {
                webDriverField.set(classField);
                isDriverTest.set(true);
                return;
            }
        }
    }

    private void setIsTestDisabled(Field driverField) {
        boolean enabled;
        if (driverField.isAnnotationPresent(ClassDriver.class)) {
            enabled = driverField.getAnnotation(ClassDriver.class).enabled();
        } else {
            enabled = driverField.getAnnotation(MethodDriver.class).enabled();
        }
        isDriverTest.set(enabled);
    }

    private void setExcludedMethods(Field driverField) {
        if (driverField.isAnnotationPresent(MethodDriver.class)) {
            excludedMethods.set(Arrays.asList(driverField.getAnnotation(MethodDriver.class).excludeMethods()));
        }
    }

    private void setClassListMap(ITestContext tc) {
        for (ITestNGMethod m : tc.getAllTestMethods()) {
            Class methodsClass = m.getRealClass();

            List<ITestNGMethod> methods = classListMap.get(methodsClass);
            if (methods == null) {
                methods = new ArrayList<ITestNGMethod>();
            }
            methods.add(m);
            classListMap.put(methodsClass, methods);
        }
    }

    private void setTestDescription(ITestResult tr) {
        String description = tr.getMethod().getDescription();
        if (description != null && !description.equals("")) {
            testDescription.set(description);
        } else {
            testDescription.set(null);
        }
    }

    private void setTestClassInstance(ITestResult tr) {
        testClassInstance.set(tr.getInstance());
    }

    private boolean isClassDriver() {
        return webDriverField.get().isAnnotationPresent(ClassDriver.class);
    }
}