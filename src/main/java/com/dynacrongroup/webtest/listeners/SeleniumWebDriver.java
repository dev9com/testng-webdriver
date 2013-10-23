package com.dynacrongroup.webtest.listeners;

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

/**
 * The purpose of this class is to start a new WebDriver instance on an annotated WebDriver variable.
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 10/1/13
 */
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

    /*==========================================================================
                                      Class Methods
      ==========================================================================*/

    private void startDriver(ITestResult result) {
        Class currentTest = result.getTestClass().getRealClass();
        Map<Class, TestClass> allClasses = driverTestClasses.get();
        TestClass currentTestClass = allClasses.get(currentTest);
        currentTestClass.setClassInstance(result.getInstance());

        try {
            if (shouldStartDriver(result, currentTestClass)) {
                currentTestClass.getWebDriverField().set(
                        currentTestClass.getClassInstance(), new ThreadLocalWebDriver(this.getClass()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void endOfMethodCleanUp(ITestResult result) {
        Class currentTest = result.getTestClass().getRealClass();
        Map<Class, TestClass> allClasses = driverTestClasses.get();
        TestClass currentTestClass = allClasses.get(currentTest).removeTestMethod(result.getMethod());
        if (shouldKillDriver(currentTestClass)) {
            killDriver(currentTestClass);
        }
    }

    private void killDriver(TestClass currentTestClass) {
        try {
            ((WebDriver) currentTestClass.getWebDriverField().get(currentTestClass.getClassInstance())).quit();
            currentTestClass.getWebDriverField().set(currentTestClass.getClassInstance(), null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new TestClass if the Class passed in contains @ClassDriver or @MethodDriver
     *
     * @param clazz the Class to check for annotation
     * @return TestClass boject
     */
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

    private boolean shouldStartDriver(ITestResult result, TestClass tc) {
        // Validate the TestClass was created, the @Driver is enabled, and the driver has not already been started.
        if (tc != null && tc.isEnabled() && !tc.isWebDriverRunning()) {
            boolean isMethodExcluded = tc.getExcludedMethods()
                    .contains(result.getMethod().getMethodName());

            if ((tc.isClassDriver()) || (tc.isMethodDriver() && !isMethodExcluded)) {
                tc.setClassInstance(result.getInstance());
                return true;
            }
        }

        return false;
    }

    private boolean shouldKillDriver(TestClass currentTestClass) {
        if (currentTestClass != null && currentTestClass.isWebDriverRunning()) {
            if (currentTestClass.isClassDriver()) {
                return currentTestClass.methodsLeftInClass() == 0;
            } else {
                return true;
            }
        }
        return false;
    }

    private void setDriverTestClasses(ITestContext testContext) {
        Map<Class, TestClass> driverClasses = new HashMap<Class, TestClass>();
        ITestNGMethod[] allTestMethods = testContext.getAllTestMethods();

        // Check every test method's class for a driver annotation
        // If we have not already added it and we find that the class contains the
        // annotation add it to the driverClasses map
        for (ITestNGMethod testMethod : allTestMethods) {
            Class currentTestClass = testMethod.getRealClass();
            TestClass tc = driverClasses.get(currentTestClass);

            // Check to see if the test class has already been created
            if (tc == null) {
                tc = getTestClass(currentTestClass);

                // If an annotation was found then add the test class
                if (tc != null) {
                    driverClasses.put(currentTestClass, tc.addTestMethod(testMethod));
                }
            // If the class has already been created then we need to add this method is contained within the class
            } else {
                driverClasses.get(currentTestClass).addTestMethod(testMethod);
            }
        }

        //Once all classes have been accounted for set in preparation of test execution
        this.driverTestClasses.set(driverClasses);
    }
}