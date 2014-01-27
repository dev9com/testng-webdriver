package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static util.Util.YAHOO_DOMAIN;
import static util.Util.HTTP_PROTOCOL;


@Test
@Listeners({SeleniumWebDriver.class})
public class TestClassDriverAnnotation {

    @ClassDriver
    WebDriver classDriver;

    @Test
    public void navigateClassToSearch() throws Exception {
        classDriver.get(HTTP_PROTOCOL + YAHOO_DOMAIN);
        String url = classDriver.getCurrentUrl();
        Assert.assertTrue(url.endsWith(YAHOO_DOMAIN), "False: " + url + " endsWith " + YAHOO_DOMAIN);
    }

    @Test(dependsOnMethods = {"navigateClassToSearch"})
    public void assertClassPersistence() {
        String url = classDriver.getCurrentUrl();
        Assert.assertTrue(url.endsWith(YAHOO_DOMAIN), "False: " + url + " endsWith " + YAHOO_DOMAIN);
    }
}
