package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.MethodDriver;
import com.dynacrongroup.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test
@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverAnnotation {

    @MethodDriver
    public WebDriver methodDriver;

    public String search = "https://www.google.com/";

    public String maps = "https://maps.google.com/";

    public String news = "https://news.google.com/";

    @Test(description = "Scenario: Assert we found www.google.com")
    public void testMethod1() {
        methodDriver.get(search);
        Assert.assertTrue(methodDriver.getCurrentUrl().equals(search));
    }

    @Test(description = "Scenario: Assert we found maps.google.com")
    public void testMethod2() {
        methodDriver.get(maps);
        Assert.assertTrue(methodDriver.getCurrentUrl().equals(maps));
    }

    @Test(description = "Scenario: Assert we found news.google.com")
    public void testMethod3() {
        methodDriver.get(news);
        Assert.assertTrue(methodDriver.getCurrentUrl().equals(news));
    }
}
