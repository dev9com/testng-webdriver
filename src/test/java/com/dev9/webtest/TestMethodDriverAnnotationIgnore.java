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
public class TestMethodDriverAnnotationIgnore {

    @MethodDriver(excludeMethods = {"assertMethodDriverUninitialized1", "assertMethodDriverUninitialized2"})
    WebDriver methodDriver;

    public void assertMethodDriverUninitialized1() throws InterruptedException {
        Assert.assertTrue(methodDriver == null, "WebDriver != null");
    }

    @Test(dependsOnMethods = {"assertMethodDriverUninitialized1"})
    public void navigateMethodDriverToSearch() {
        methodDriver.get(HTTP_PROTOCOL + YAHOO_DOMAIN);
        String url = methodDriver.getCurrentUrl();
        Assert.assertTrue(url.endsWith(YAHOO_DOMAIN), "False: " + url + " endsWith " + YAHOO_DOMAIN);
    }

    @Test(dependsOnMethods = {"navigateMethodDriverToSearch"})
    public void assertMethodDriverUninitialized2() {
        Assert.assertTrue(methodDriver == null, "WebDriver != null");
    }
}
