package com.ore.saucedemo.pages;

import com.ore.saucedemo.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Shared behaviour for every page object.
 *
 * <p>All interaction goes through the explicit-wait helpers here rather than raw
 * {@code driver.findElement} calls, which is what keeps the suite free of
 * {@code Thread.sleep} and of the flakiness that comes with it.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.explicitTimeout()));
    }

    /** Each page knows the one element that proves it has finished loading. */
    protected abstract By uniqueLocator();

    /** True when this page is the one currently on screen. */
    public boolean isLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(uniqueLocator())).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    protected void type(By locator, String text) {
        WebElement field = waitForVisible(locator);
        field.clear();
        field.sendKeys(text);
    }

    protected String textOf(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected void selectByValue(By locator, String value) {
        new Select(waitForVisible(locator)).selectByValue(value);
    }

    protected String selectedOption(By locator) {
        return new Select(waitForVisible(locator)).getFirstSelectedOption().getText().trim();
    }

    /**
     * Presence check that does not wait the full timeout - used for assertions about
     * things that should be absent, where waiting 10 seconds for every check would make
     * the suite crawl.
     */
    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String pageTitle() {
        return driver.getTitle();
    }
}
