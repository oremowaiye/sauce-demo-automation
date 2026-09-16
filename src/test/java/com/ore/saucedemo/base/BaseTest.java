package com.ore.saucedemo.base;

import com.ore.saucedemo.config.ConfigReader;
import com.ore.saucedemo.driver.BrowserType;
import com.ore.saucedemo.driver.DriverFactory;
import com.ore.saucedemo.model.TestUser;
import com.ore.saucedemo.pages.InventoryPage;
import com.ore.saucedemo.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Lifecycle shared by every UI test: one fresh browser per test method, always closed.
 *
 * <p>A browser per method costs a little time but buys complete isolation - no test can
 * be made to pass or fail by the state another one left behind, which is what makes the
 * suite safe to run in parallel and trustworthy in CI.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browserParameter) {
        String browserName = (browserParameter == null || browserParameter.isBlank())
                ? ConfigReader.get("browser")
                : browserParameter;

        driver = DriverFactory.createDriver(
                BrowserType.from(browserName),
                ConfigReader.getBoolean("headless"));

        loginPage = new LoginPage(driver).open();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    /** Shortcut for the many tests whose starting point is "signed in as a normal user". */
    protected InventoryPage signInAsStandardUser() {
        return loginPage.loginAs(TestUser.STANDARD);
    }
}
