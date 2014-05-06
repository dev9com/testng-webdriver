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
        checkForWebDriverField(tr);

        if (isDriverTest.get()) {
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
                if (!isDriverRunning(tr)) {
                    initializeDriver(tr);
                }
            } else {
                setDriverNull(tr);
            }
        }
    }

    private void initializeDriver(ITestResult tr) {
        try {
            webDriverField.get().set(
                    tr.getInstance(), new ThreadLocalWebDriver(getRealTestClass(tr), getTestDescription(tr)));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setDriverFieldAccessible() {
        webDriverField.get().setAccessible(true);
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
                quitDriver(tr);
            }
        } else {
            quitDriver(tr);
        }
    }

    private void quitDriver(ITestResult tr) {
        try {
            WebDriver driver = (WebDriver) webDriverField.get().get(tr.getInstance());
            driver.quit();
        } catch (Exception e) {/* Ignore if driver has already quit */ }
    }

    private void setDriverNull(ITestResult tr) {
        try {
            webDriverField.get().set(tr.getInstance(), null);
        } catch (Exception e) {/* Ignore error */ }
    }

    private boolean isTestExcluded(ITestResult tr) {
        return excludedMethods.get() != null && excludedMethods.get().contains(tr.getMethod().getMethodName());
    }

    private boolean isDriverRunning(ITestResult tr) {
        Field webDriver = webDriverField.get();
        if (webDriver == null) return false;

        // Horrid way to check if .quit() was called on the driver.
        try {
            WebDriver driver = (WebDriver) webDriver.get(tr.getInstance());
            driver.getTitle();
            return true;
        } catch (Exception e) { return false; }
    }

    /**
     * Returns the real Class from TestNG's TestResult object
     *
     * @param tr ITestResult from TestNG
     * @return the class the current test method is running in
     */
    private Class getRealTestClass(ITestResult tr) {
        return tr.getTestClass().getRealClass();
    }

    /**
     * Populates the webDriverField with the Driver Annotated field and sets
     * isDriverTest to true if the Annotation was found.
     *
     * TODO: If the users defines multiple drivers within the testClass fail.
     *
     * @param tr ITestResult from TestNG
     */
    private void checkForWebDriverField(ITestResult tr) {
        Class testClass = getRealTestClass(tr);
        for (Field classField : testClass.getDeclaredFields()) {
            if (classField.isAnnotationPresent(ClassDriver.class)) {
                setWebDriverField(classField);
                isDriverTest.set(classField.getAnnotation(ClassDriver.class).enabled());
                return;
            } else if (classField.isAnnotationPresent(MethodDriver.class)) {
                setWebDriverField(classField);
                isDriverTest.set(classField.getAnnotation(MethodDriver.class).enabled());
                excludedMethods.set(Arrays.asList(classField.getAnnotation(MethodDriver.class).excludeMethods()));
                return;
            }
        }
    }

    private void setWebDriverField(Field classField) {
        webDriverField.set(classField);
        isDriverTest.set(true);
        setDriverFieldAccessible();
    }

    /**
     * Populates the ClassListMap with key=testClass, value=list of testMethods
     *
     * @param tc ITestContext from TestNG
     */
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

    /**
     * Sets the testDescription to the description on the testMethod
     *
     * @param tr ITestResult from TestNG
     */
    private String getTestDescription(ITestResult tr) {
        String description = tr.getMethod().getDescription();
        if (description != null && !description.equals("")) {
            return description;
        } else {
            return null;
        }
    }

    private boolean isClassDriver() {
        return webDriverField.get() != null && webDriverField.get().isAnnotationPresent(ClassDriver.class);
    }
}