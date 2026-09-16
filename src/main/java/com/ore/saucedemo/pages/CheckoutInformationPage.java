package com.ore.saucedemo.pages;

import com.ore.saucedemo.model.CheckoutInfo;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Checkout step one: the buyer's name and postcode. */
public class CheckoutInformationPage extends BasePage {

    private static final By FORM = By.cssSelector(".checkout_info");
    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By CANCEL_BUTTON = By.id("cancel");
    private static final By ERROR_MESSAGE = By.cssSelector("h3[data-test='error']");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By uniqueLocator() {
        return FORM;
    }

    /** Fills and submits in one go - the happy path most tests need. */
    @Step("Complete the checkout form and continue")
    public CheckoutOverviewPage submit(CheckoutInfo info) {
        fill(info);
        click(CONTINUE_BUTTON);
        return new CheckoutOverviewPage(driver);
    }

    /**
     * Submits whatever is in the form without assuming it is accepted, so validation
     * tests can stay on this page and read the error banner.
     */
    @Step("Submit the checkout form with first='{firstName}' last='{lastName}' postcode='{postalCode}'")
    public CheckoutInformationPage submitExpectingError(String firstName, String lastName, String postalCode) {
        fill(new CheckoutInfo(firstName, lastName, postalCode));
        click(CONTINUE_BUTTON);
        return this;
    }

    public CheckoutInformationPage fill(CheckoutInfo info) {
        if (!info.firstName().isEmpty()) {
            type(FIRST_NAME, info.firstName());
        }
        if (!info.lastName().isEmpty()) {
            type(LAST_NAME, info.lastName());
        }
        if (!info.postalCode().isEmpty()) {
            type(POSTAL_CODE, info.postalCode());
        }
        return this;
    }

    public String errorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public boolean hasErrorMessage() {
        return isPresent(ERROR_MESSAGE);
    }

    @Step("Cancel checkout")
    public CartPage cancel() {
        click(CANCEL_BUTTON);
        return new CartPage(driver);
    }
}
