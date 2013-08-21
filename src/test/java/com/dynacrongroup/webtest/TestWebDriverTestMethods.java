package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.reporters.SauceTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The purpose of this class is to verify {@link WebDriverForMethod} is starting the driver before each test method
 * and shutting it down afterward.
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 8/7/13
 */
//@Listeners({SauceTestListener.class})
public class TestWebDriverTestMethods extends WebDriverForMethod {
    private WebDriver firstDriver;

    @Test
    public void testDriverStarted() {
        this.firstDriver = driver;
        String url = "http://www.google.com/";
        driver.get(url);
        Assert.assertTrue(url.equals(driver.getCurrentUrl()));
    }

    @Test(dependsOnMethods = {"testDriverStarted"})
    public void testFirstDriverQuit() {
        Assert.assertFalse(firstDriver.equals(driver));
    }
}
