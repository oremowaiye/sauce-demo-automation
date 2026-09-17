package com.ore.saucedemo.driver;

import com.ore.saucedemo.config.ConfigReader;
import com.ore.saucedemo.exceptions.FrameworkException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates and hands out WebDriver instances.
 *
 * <p>The driver is held in a {@link ThreadLocal} so the suite can run classes in parallel
 * without tests stealing each other's browser. Driver binaries are resolved by Selenium
 * Manager (built into Selenium 4.6+), so there is nothing to download or check in.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    static {
        // Selenium logs a CDP-version warning for every driver it starts whenever the
        // installed Chrome is newer than the DevTools mappings the client ships with.
        // Nothing here uses CDP, so the warning is pure noise that buries real failures.
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
    }

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        return createDriver(
                BrowserType.from(ConfigReader.get("browser")),
                ConfigReader.getBoolean("headless"));
    }

    public static WebDriver createDriver(BrowserType browser, boolean headless) {
        if (DRIVER.get() != null) {
            quitDriver();
        }

        WebDriver driver = switch (browser) {
            case CHROME -> new ChromeDriver(chromeOptions(headless));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(headless));
            case EDGE -> new EdgeDriver(edgeOptions(headless));
        };

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.pageLoadTimeout()));
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new FrameworkException("No WebDriver for this thread - createDriver() was never called");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-search-engine-choice-screen");
        options.addArguments("--disable-notifications");
        // SauceDemo's demo credentials trigger Chrome's "breached password" bubble,
        // which can sit on top of the UI and break clicks. Turn the password manager off.
        options.addArguments("--disable-features=PasswordLeakDetection,AutofillServerCommunication");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }
}
