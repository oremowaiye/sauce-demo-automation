package com.ore.saucedemo.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Entry point for the Gherkin layer.
 *
 * <p>These scenarios are not a second test suite. They call the same page objects the
 * TestNG tests do. Gherkin is worth the extra layer only where someone who does not
 * read Java needs to read the scenario, which here means the rules around signing in
 * and buying something.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.ore.saucedemo.bdd.steps", "com.ore.saucedemo.bdd.hooks"},
        plugin = {
                "pretty",
                "summary",
                "html:target/cucumber/cucumber-report.html",
                "json:target/cucumber/cucumber-report.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
}
