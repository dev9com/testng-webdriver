package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.MethodDriver;
import com.dynacrongroup.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test
@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverAnnotationIgnore {

    @MethodDriver(excludeMethods = {"assertMethodDriverUninitialized1", "assertMethodDriverUninitialized2"})
    WebDriver methodDriver;

    public String search = "http://www.yahoo.com/";

    public void assertMethodDriverUninitialized1() throws InterruptedException {
        Assert.assertTrue(methodDriver == null, "WebDriver != null");
    }

    @Test(dependsOnMethods = {"assertMethodDriverUninitialized1"})
    public void navigateMethodDriverToSearch() {
        methodDriver.get(search);
        String url = methodDriver.getCurrentUrl();
        Assert.assertTrue(url.equals(search), url + " != " + search);
    }

    @Test(dependsOnMethods = {"navigateMethodDriverToSearch"})
    public void assertMethodDriverUninitialized2() {
        Assert.assertTrue(methodDriver == null, "WebDriver != null");
    }
}
