package com.dev9.webtest;

import com.dev9.webtest.annotation.ClassDriver;
import com.dev9.webtest.listeners.SeleniumWebDriver;
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
