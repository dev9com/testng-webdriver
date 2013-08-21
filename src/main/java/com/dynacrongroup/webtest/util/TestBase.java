package com.dynacrongroup.webtest.util;

import com.dynacrongroup.webtest.driver.ThreadLocalWebDriver;

/**
 * The purpose of this class is to provide the required functions to our WebDriverTest classes.
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 8/7/13
 */
public interface TestBase {
    public void startWebDriver();
    public void killWebDriver();
    public ThreadLocalWebDriver unwrapDriver();
}
