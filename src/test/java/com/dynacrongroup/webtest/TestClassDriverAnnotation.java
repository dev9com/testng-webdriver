package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


@Test
@Listeners({SeleniumWebDriver.class})
public class TestClassDriverAnnotation {

    @ClassDriver
    WebDriver classDriver;

    public String search = "http://www.yahoo.com/";

    @Test
    public void navigateClassToSearch() throws Exception {
        classDriver.get(search);
        String url = classDriver.getCurrentUrl();
        Assert.assertTrue(url.equals(search), url + " != " + search);
    }

    @Test(dependsOnMethods = {"navigateClassToSearch"})
    public void assertClassPersistence() {
        String url = classDriver.getCurrentUrl();
        Assert.assertTrue(url.equals(search), url + " != " + search);
    }
}
