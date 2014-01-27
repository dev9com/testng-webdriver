package util;

import org.testng.annotations.DataProvider;

/**
 * The purpose of this class is to provide utility functions during testing.
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 11/4/13
 */
public final class Util {
    public static final String HTTP_PROTOCOL = "http://";
    public static final String YAHOO_DOMAIN = "www.yahoo.com/";

    public static void sleep() throws InterruptedException {
        Thread.sleep(2000);
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProvider() {
        return new Object[][] {
                {"http://www.google.com", 1},
                {"http://www.yahoo.com/", 2},
                {"http://www.wikipedia.org/", 3},
                {"http://github.com/", 4}
        };
    }
}
