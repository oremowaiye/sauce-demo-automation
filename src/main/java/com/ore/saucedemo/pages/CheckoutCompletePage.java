package com.ore.saucedemo.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The order confirmation screen. */
public class CheckoutCompletePage extends BasePage {

    private static final By COMPLETE_CONTAINER = By.id("checkout_complete_container");
    private static final By HEADER = By.className("complete-header");
    private static final By BODY_TEXT = By.className("complete-text");
    private static final By PONY_IMAGE = By.className("pony_express");
    private static final By BACK_HOME_BUTTON = By.id("back-to-products");

    private final HeaderComponent header;

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    @Override
    protected By uniqueLocator() {
        return COMPLETE_CONTAINER;
    }

    public HeaderComponent header() {
        return header;
    }

    public String confirmationHeader() {
        return textOf(HEADER);
    }

    public String confirmationText() {
        return textOf(BODY_TEXT);
    }

    public boolean hasConfirmationImage() {
        return isPresent(PONY_IMAGE);
    }

    @Step("Return to the product list")
    public InventoryPage backHome() {
        click(BACK_HOME_BUTTON);
        return new InventoryPage(driver);
    }
}
