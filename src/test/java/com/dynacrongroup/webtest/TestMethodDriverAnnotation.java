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
    WebDriver methodDriver;

    public String search = "http://www.yahoo.com/";

    @Test(description = "Scenario: Assert we found www.yahoo.com")
    public void navigateMethodToSearch() {
        methodDriver.get(search);
        String url = methodDriver.getCurrentUrl();
        Assert.assertTrue(url.equals(search), url + " != " + search);
    }

    @Test(description = "Scenario: Assert we opened a new browser",
          dependsOnMethods = {"navigateMethodToSearch"})
    public void assertNoPersistence() {
        String url = methodDriver.getCurrentUrl();
        Assert.assertFalse(url.equals(search), url + " == " + search);
    }
}
