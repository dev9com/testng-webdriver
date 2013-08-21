package com.dynacrongroup.webtest.reporters;

import com.dynacrongroup.webtest.conf.SauceLabsCredentials;
import com.dynacrongroup.webtest.driver.ThreadLocalWebDriver;
import com.dynacrongroup.webtest.sauce.SauceREST;
import com.dynacrongroup.webtest.util.TestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * The purpose of this class is to alert SauceLabs (through sauceREST) of passed or failed jobs. Note: At some point
 * it may be beneficial to change this to update {@link #sauceREST} real time from
 * {@link #onTestFailure(org.testng.ITestResult)}. Current behavior (copied from the junit-webdriver) is to send a
 * {@code sauceREST.jobFailed();} or {@code sauceREST.jobPassed();} once all tests are complete.
 *
 * @author <a href="mailto:Justin.Graham@dynacrongroup.com">Justin Graham</a>
 * @since 8/8/13
 */
public class SauceTestListener extends TestListenerAdapter {
    private static ThreadLocal<Boolean> failed = new ThreadLocal<Boolean>();
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private ThreadLocalWebDriver threadLocalWebDriver;
    private SauceREST sauceREST;

    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        failed.set(false);
    }

    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart(result);

        if (result.getInstance() instanceof TestBase) {
            this.threadLocalWebDriver = ((TestBase) result.getInstance()).unwrapDriver();
            if (threadLocalWebDriver.getSessionId() != null) {
                LOG.debug(String.format("SessionID=%1$s TestName=%2$s",
                                        threadLocalWebDriver.getSessionId(),
                                        result.getMethod().getMethodName()));
            }
        }

        try
        {
            this.sauceREST =
                    new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey());
        } catch (RuntimeException re) {
            //TODO: Add exact requirements to error message.
            throw new RuntimeException(re.getMessage() + " Required on PATH.");
        }
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        failed.set(true);
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        if (threadLocalWebDriver != null && threadLocalWebDriver.isRemote()) {
            if (failed.get()) {
                sauceREST.jobFailed(getJobId());
            } else {
                sauceREST.jobPassed(getJobId());
            }
        }
    }

    private String getJobId() {
        return threadLocalWebDriver.getJobId();
    }
}
