package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.driver.ThreadLocalWebDriver;
import com.dynacrongroup.webtest.util.TestBase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * The purpose of this class is to start and stop a ThreadLocal WebDriver before and after the test class.
 * That is, if this class is extended all unit tests within the class will have access to the same WebDriver
 * instance. The reason behind this is to allow dependant test methods to be writen using the WebDriver.
 *
 * <pre> <b>Example:</b>
 * {@code
 *  public class TestClass extends WebDriverForClass {
 *      @Test
 *      public void openUrl() {
 *          String url =  "http://www.google.com/";
 *          driver.get(url);
 *          Assert.assertTrue(url.equals(driver.getCurrentUrl()));
 *      }
 *
 *      @Test(dependsOnMethods = {"openUrl"})
 *      public void searchDynacronGroup() {
 *          WebElement googleSearch = driver.findElement(By.name("q"));
 *          googleSearch.sendKeys("Dynacron Group");
 *          googleSearch.submit();
 *          Assert.assertTrue(driver.getCurrentUrl().endsWith("q=Dynacron+Group"));
 *      }
 *  }
 * }
 * </pre>
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 8/7/13
 */
public class WebDriverForClass implements TestBase {

    protected WebDriver driver;

    @BeforeClass
    @Override
    public void startWebDriver() {
        this.driver = new ThreadLocalWebDriver(this.getClass());
    }

    @AfterClass
    @Override
    public void killWebDriver() {
        driver.quit();
    }

    @Override
    public ThreadLocalWebDriver unwrapDriver() {
        return (ThreadLocalWebDriver) driver;
    }
}
