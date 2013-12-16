package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.MethodDriver;
import com.dynacrongroup.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import util.Util;

import static util.Util.sleep;

@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverDataProvider {

    @MethodDriver
    public WebDriver driver;

    @Test(threadPoolSize = 4, dataProvider = "dataProvider", dataProviderClass = Util.class)
    public void testDataProviderValues(String url, int i) throws Exception {
        driver.get(url);

        String currentUrl = driver.getCurrentUrl();

        switch (i) {
            case 1:
                Assert.assertTrue(currentUrl.contains("www.google.com"));
                sleep();
                break;
            case 2:
                Assert.assertTrue(currentUrl.contains("www.yahoo.com"));
                sleep();
                break;
            case 3:
                Assert.assertTrue(currentUrl.contains("www.wikipedia.org"));
                sleep();
                break;
            case 4:
                Assert.assertTrue(currentUrl.contains("github.com/"));
                sleep();
                break;
            default:
                throw new IllegalStateException("[" + i + "] is an unknown url digit!");
        }
    }
}
