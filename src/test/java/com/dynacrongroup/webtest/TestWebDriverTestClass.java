package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.reporters.SauceTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The purpose of this class is to verify {@link WebDriverForClass} is starting the driver before our test class
 * and shutting it down afterward.
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 8/7/13
 */
//@Listeners({SauceTestListener.class})
public class TestWebDriverTestClass extends WebDriverForClass {
    String url = "http://www.google.com/";


    @Test(description = "Tests the driver started correctly and navigated to google.com")
    public void testDriverStarted() {
        driver.get(url);
        Assert.assertTrue(url.equals(driver.getCurrentUrl()));
    }

    @Test(description = "Tests the driver started before this class is still running",
          dependsOnMethods = {"testDriverStarted"})
    public void testDriverIsStillRunning() {
        Assert.assertTrue(url.equals(driver.getCurrentUrl()));
    }

    @Test(description = "Tests the driver is still available and is functioning as expected",
          dependsOnMethods = {"testDriverIsStillRunning"})
    public void searchDynacronGroup() {
        WebElement googleSearch = driver.findElement(By.name("q"));
        googleSearch.sendKeys("Dynacron Group");
        googleSearch.submit();
        Assert.assertTrue(driver.getCurrentUrl().endsWith("q=Dynacron+Group"));
    }
}
