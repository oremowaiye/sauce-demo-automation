package com.ore.saucedemo.bdd.hooks;

import com.ore.saucedemo.driver.DriverFactory;
import com.ore.saucedemo.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/** One browser per scenario, always closed, with a screenshot on failure. */
public class Hooks {

    @Before
    public void startBrowser(Scenario scenario) {
        DriverFactory.createDriver();
        System.out.printf("  -> Scenario: %s%n", scenario.getName());
    }

    @After
    public void stopBrowser(Scenario scenario) {
        if (scenario.isFailed() && DriverFactory.hasDriver()) {
            ScreenshotUtils.capture(DriverFactory.getDriver(), scenario.getName());
        }
        DriverFactory.quitDriver();
    }
}
