package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.driver.ThreadLocalWebDriver;
import com.dynacrongroup.webtest.util.TestBase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * The purpose of this class is to start and close a ThreadLocal WebDriver before and after each test method.
 * This is the same behavior as you would expect in jUnit using the @ClassRule and
 * {@link com.dynacrongroup.webtest.rule.DriverClassRule}
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 8/7/13
 */
public class WebDriverForMethod implements TestBase {

    protected WebDriver driver;

    @BeforeMethod
    @Override
    public void startWebDriver() {
        this.driver = new ThreadLocalWebDriver(this.getClass());
    }

    @AfterMethod
    @Override
    public void killWebDriver() {
        driver.quit();
    }

    @Override
    public ThreadLocalWebDriver unwrapDriver() {
        return (ThreadLocalWebDriver) driver;
    }
}
