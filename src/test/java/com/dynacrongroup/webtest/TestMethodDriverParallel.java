package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.MethodDriver;
import com.dynacrongroup.webtest.listeners.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverParallel {

    @MethodDriver
    public WebDriver driver;

    @DataProvider(parallel = true)
    public Object[][] dataProvider() {
        return new Object[][] {
                {"http://www.google.com", 1},
                {"http://www.yahoo.com/", 2},
                {"https://www.facebook.com/", 3},
                {"https://twitter.com/", 4}
        };
    }

    @Test(threadPoolSize = 4, dataProvider = "dataProvider")
    public void testMethod1(String url, int i) throws Exception {
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
                Assert.assertTrue(currentUrl.contains("www.facebook.com"));
                sleep();
                break;
            case 4:
                Assert.assertTrue(currentUrl.contains("twitter.com"));
                sleep();
                break;
            default:
                throw new IllegalStateException("[" + i + "] is an unknown url digit!");
        }
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(2000);
    }
}
