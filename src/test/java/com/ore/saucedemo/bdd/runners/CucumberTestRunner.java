package com.ore.saucedemo.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Entry point for the Gherkin layer.
 *
 * <p>The BDD scenarios are not a second test suite - they call the same page objects as
 * the TestNG tests. Gherkin earns its place where a scenario is worth reading by someone
 * who does not read Java: the business rules around signing in and buying something.
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
