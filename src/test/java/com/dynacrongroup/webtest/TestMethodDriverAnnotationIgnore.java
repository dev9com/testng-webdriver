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

    @MethodDriver(excludeMethods = {"testMethod1", "testMethod3"})
    public WebDriver methodDriver;

    public String search = "https://www.google.com/";

    public void testMethod1() throws InterruptedException {
        Assert.assertTrue(methodDriver == null);
    }

    public void testMethod2() {
        methodDriver.get(search);
        Assert.assertTrue(methodDriver.getCurrentUrl().equals(search));
    }

    @Test
    public void testMethod3() {
        Assert.assertTrue(methodDriver == null);
    }
}
