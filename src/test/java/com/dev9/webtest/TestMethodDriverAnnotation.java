package com.dev9.webtest;

import com.dev9.webtest.annotation.MethodDriver;
import com.dev9.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static util.Util.HTTP_PROTOCOL;
import static util.Util.YAHOO_DOMAIN;

@Test
@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverAnnotation {

    @MethodDriver
    WebDriver methodDriver;

    @Test(description = "Scenario: Assert we found www.yahoo.com")
    public void navigateMethodToSearch() {
        methodDriver.get(HTTP_PROTOCOL + YAHOO_DOMAIN);
        String url = methodDriver.getCurrentUrl();
        Assert.assertTrue(url.endsWith(YAHOO_DOMAIN), "False: " + url + " endsWith " + YAHOO_DOMAIN);
    }

    @Test(description = "Scenario: Assert we opened a new browser",
          dependsOnMethods = {"navigateMethodToSearch"})
    public void assertNoPersistence() {
        String url = methodDriver.getCurrentUrl();
        Assert.assertFalse(url.endsWith(YAHOO_DOMAIN), "True: " + url + " endsWith " + YAHOO_DOMAIN);
    }
}
