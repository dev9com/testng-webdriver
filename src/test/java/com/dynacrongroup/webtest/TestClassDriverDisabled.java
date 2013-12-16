package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test
@Listeners({SeleniumWebDriver.class})
public class TestClassDriverDisabled {
    @ClassDriver(enabled = false)
    public WebDriver driver;

    public void assertClassDriverUninitialized() {
        Assert.assertTrue(driver == null, "WebDriver != null");
    }
}
