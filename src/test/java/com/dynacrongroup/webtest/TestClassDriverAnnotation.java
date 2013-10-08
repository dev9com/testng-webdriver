package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.ClassDriver;
import com.dynacrongroup.webtest.reporters.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


@Test
@Listeners({SeleniumWebDriver.class})
public class TestClassDriverAnnotation {

    @ClassDriver
    public WebDriver classDriver;

    public String search = "https://www.google.com/";

    public String maps = "https://maps.google.com/";

    public void testMethod1() throws InterruptedException {
        classDriver.get(search);
        Assert.assertTrue(classDriver.getCurrentUrl().equals(search));
    }

    public void testMethod2() {
        Assert.assertTrue(classDriver.getCurrentUrl().equals(search));
    }

    public void testMethod3() {
        classDriver.get(maps);
        Assert.assertTrue(classDriver.getCurrentUrl().equals(maps));
    }
}
