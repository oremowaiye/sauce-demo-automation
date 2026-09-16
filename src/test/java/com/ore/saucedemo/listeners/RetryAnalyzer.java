package com.ore.saucedemo.listeners;

import com.ore.saucedemo.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test a small, configurable number of times.
 *
 * <p>Retries are a pragmatic guard against genuinely infrastructural flakiness (a dropped
 * connection to the grid, a cold start). They are deliberately capped at one by default:
 * a test that only passes on the second attempt is a bug report, not a green build.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private final int maxRetries = ConfigReader.getInt("retry.count");
    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < maxRetries) {
            attempts++;
            System.out.printf("  RETRY %s (attempt %d of %d)%n",
                    result.getName(), attempts + 1, maxRetries + 1);
            return true;
        }
        return false;
    }
}
