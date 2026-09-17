package com.ore.saucedemo.pages;

import com.ore.saucedemo.config.ConfigReader;
import com.ore.saucedemo.model.TestUser;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The sign-in screen. Every UI test starts here. */
public class LoginPage extends BasePage {

    private static final By USERNAME_FIELD = By.id("user-name");
    private static final By PASSWORD_FIELD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("h3[data-test='error']");
    private static final By ERROR_CLOSE_BUTTON = By.cssSelector("button.error-button");
    private static final By LOGIN_LOGO = By.className("login_logo");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By uniqueLocator() {
        return LOGIN_LOGO;
    }

    @Step("Open the SauceDemo login page")
    public LoginPage open() {
        driver.get(ConfigReader.baseUrl());
        waitForVisible(LOGIN_LOGO);
        return this;
    }

    @Step("Sign in as {user}")
    public InventoryPage loginAs(TestUser user) {
        submitCredentials(user.username(), user.password());
        return new InventoryPage(driver);
    }

    @Step("Submit credentials: '{username}' / '{password}'")
    public LoginPage submitCredentials(String username, String password) {
        if (!username.isEmpty()) {
            type(USERNAME_FIELD, username);
        }
        if (!password.isEmpty()) {
            type(PASSWORD_FIELD, password);
        }
        click(LOGIN_BUTTON);
        return this;
    }

    public String errorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public boolean hasErrorMessage() {
        return isPresent(ERROR_MESSAGE);
    }

    public LoginPage dismissError() {
        click(ERROR_CLOSE_BUTTON);
        return this;
    }
}
