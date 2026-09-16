package com.ore.saucedemo.listeners;

import com.ore.saucedemo.config.ConfigReader;
import com.ore.saucedemo.driver.DriverFactory;
import com.ore.saucedemo.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/** Console progress plus a screenshot attached to the Allure report on every failure. */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.printf("==> Suite '%s' starting%n", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.printf("  -> %s%n", displayName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.printf("  PASS %s (%d ms)%n", displayName(result), duration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.printf("  FAIL %s (%d ms): %s%n",
                displayName(result), duration(result), rootMessage(result));

        if (ConfigReader.getBoolean("screenshot.on.failure") && DriverFactory.hasDriver()) {
            ScreenshotUtils.capture(DriverFactory.getDriver(), displayName(result));
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.printf("  SKIP %s%n", displayName(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.printf("==> Suite '%s' finished: %d passed, %d failed, %d skipped%n",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    private String displayName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();
    }

    private long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }

    private String rootMessage(ITestResult result) {
        Throwable throwable = result.getThrowable();
        return throwable == null ? "no exception recorded" : throwable.getMessage();
    }
}
