package com.dev9.webtest;

import com.dev9.webtest.annotation.MethodDriver;
import com.dev9.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test
@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverDisabled {
    @MethodDriver(enabled = false)
    public WebDriver driver;

    public void assertMethodDriverUninitialized() {
        Assert.assertTrue(driver == null, "WebDriver != null");
    }
}
