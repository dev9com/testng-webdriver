package com.dynacrongroup.webtest.reporters;

import com.dynacrongroup.webtest.TestClass;
import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.annotation.MethodDriver;
import com.dynacrongroup.webtest.driver.ThreadLocalWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SeleniumWebDriver extends TestListenerAdapter {
    private ThreadLocal<Map<Class, TestClass>> driverTestClasses = new ThreadLocal<Map<Class, TestClass>>();

    /*==========================================================================
                                         Start
      ==========================================================================*/

    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        setDriverTestClasses(testContext);
    }


    // Currently an issue with running multiple classes at the same time...
    // https://groups.google.com/forum/#!topic/testng-users/6zM7fOPqKWE
    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        startDriver(tr);
    }

    /*==========================================================================
                                         Finish
      ==========================================================================*/

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        endOfMethodCleanUp(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        endOfMethodCleanUp(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        endOfMethodCleanUp(tr);
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        endOfClassCleanUp();
    }

    /*==========================================================================
                                      Class Methods
      ==========================================================================*/

    private void startDriver(ITestResult result) {
        Class currentTest = result.getTestClass().getRealClass();
        Map<Class, TestClass> allClasses = driverTestClasses.get();
        TestClass currentTestClass = allClasses.get(currentTest);

        if (shouldStartDriver(result, currentTestClass)) {
            try {
                System.out.println("Starting Driver for: " + currentTest.getSimpleName() + "()." + result.getMethod().getMethodName() + "()");
                currentTestClass.getWebDriverField().set(
                        result.getInstance(), new ThreadLocalWebDriver(this.getClass()));
                currentTestClass.setWebDriverRunning(true);
                currentTestClass.addInvokedMethod(result.getMethod());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void endOfMethodCleanUp(ITestResult result) {
        Class currentTest = result.getTestClass().getRealClass();
        Map<Class, TestClass> allClasses = driverTestClasses.get();
        TestClass currentTestClass = allClasses.get(currentTest);
        if (shouldKillDriver(result, currentTestClass)) {
            killDriver(currentTestClass);
        }
    }

    private void endOfClassCleanUp() {
        for (TestClass tc : driverTestClasses.get().values()) {
            if (tc.isWebDriverRunning() && tc.getDriverAnnotation().equals(ClassDriver.class)) {
                killDriver(tc);
            }
        }
    }

    private void killDriver(TestClass currentTestClass) {
        try {
            currentTestClass.setWebDriverRunning(false);
            ((WebDriver) currentTestClass.getWebDriverField().get(currentTestClass.getClassInstance())).quit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private TestClass getTestClass(Class clazz) {
        for (Field classField : clazz.getFields()) {
            if (classField.isAnnotationPresent(ClassDriver.class)) {
                return new TestClass()
                        .setDriverAnnotation(ClassDriver.class)
                        .setWebDriverField(classField)
                        .setTestClass(clazz)
                        .setEnabled(classField.getAnnotation(ClassDriver.class).enabled());
            } else if (classField.isAnnotationPresent(MethodDriver.class)) {
                MethodDriver md = classField.getAnnotation(MethodDriver.class);
                return new TestClass()
                        .setDriverAnnotation(MethodDriver.class)
                        .setWebDriverField(classField)
                        .setTestClass(clazz)
                        .setEnabled(md.enabled())
                        .setExcludedMethods(md.excludeMethods());
            }
        }

        return null;
    }

    private boolean shouldStartDriver(ITestResult result, TestClass currentTestClass) {
        if (currentTestClass != null && currentTestClass.isEnabled()) {
            boolean isClassDriver = currentTestClass.getDriverAnnotation().equals(ClassDriver.class);
            boolean isMethodDriver = currentTestClass.getDriverAnnotation().equals(MethodDriver.class);
            boolean isDriverRunning = currentTestClass.isWebDriverRunning();
            boolean isMethodExcluded = currentTestClass.getExcludedMethods()
                    .contains(result.getMethod().getMethodName());

            if ((isClassDriver && !isDriverRunning) || (isMethodDriver && !isDriverRunning && !isMethodExcluded)) {
                currentTestClass.setClassInstance(result.getInstance());
                return true;
            }
        }

        return false;
    }

    private boolean shouldKillDriver(ITestResult result, TestClass currentTestClass) {
        // case 1: Do not kill driver if it was not started: null
        // case 2: Do not kill driver if it is a ClassDriver
        // case 3: Do not kill driver if it didn't start before the method

        return currentTestClass != null
                && !currentTestClass.getDriverAnnotation().equals(ClassDriver.class)
                && !currentTestClass.getExcludedMethods().contains(result.getMethod().getMethodName());

    }

    private void setDriverTestClasses(ITestContext testContext) {
        Map<Class, TestClass> driverClasses = new HashMap<Class, TestClass>();
        ITestNGMethod[] allTestMethods = testContext.getAllTestMethods();

        // Check every test method's class for a driver annotation
        // If we have not already added it and we find that the class contains the
        // annotation add it to the driverClasses map
        for (ITestNGMethod testMethod : allTestMethods) {
            Class currentTestClass = testMethod.getRealClass();
            if (driverClasses.get(currentTestClass) == null) {
                TestClass tc = getTestClass(currentTestClass);
                if (tc != null) {
                    driverClasses.put(currentTestClass, getTestClass(currentTestClass));
                }
            }
        }

        //Once all classes have been accounted for set in preparation of test execution
        this.driverTestClasses.set(driverClasses);
    }
}