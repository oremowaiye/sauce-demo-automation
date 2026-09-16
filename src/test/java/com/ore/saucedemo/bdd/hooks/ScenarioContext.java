package com.ore.saucedemo.bdd.hooks;

import com.ore.saucedemo.pages.CartPage;
import com.ore.saucedemo.pages.CheckoutCompletePage;
import com.ore.saucedemo.pages.CheckoutInformationPage;
import com.ore.saucedemo.pages.CheckoutOverviewPage;
import com.ore.saucedemo.pages.InventoryPage;
import com.ore.saucedemo.pages.LoginPage;

/**
 * State shared between step-definition classes within a single scenario.
 *
 * <p>Cucumber creates one instance per scenario and injects it into every step class that
 * asks for it (PicoContainer), which keeps the steps free of static state and safe to run
 * scenario by scenario.
 */
public class ScenarioContext {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutInformationPage checkoutInformationPage;
    private CheckoutOverviewPage checkoutOverviewPage;
    private CheckoutCompletePage checkoutCompletePage;

    public LoginPage loginPage() {
        return loginPage;
    }

    public void setLoginPage(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    public InventoryPage inventoryPage() {
        return inventoryPage;
    }

    public void setInventoryPage(InventoryPage inventoryPage) {
        this.inventoryPage = inventoryPage;
    }

    public CartPage cartPage() {
        return cartPage;
    }

    public void setCartPage(CartPage cartPage) {
        this.cartPage = cartPage;
    }

    public CheckoutInformationPage checkoutInformationPage() {
        return checkoutInformationPage;
    }


    public void setCheckoutInformationPage(CheckoutInformationPage page) {
        this.checkoutInformationPage = page;
    }

    public CheckoutOverviewPage checkoutOverviewPage() {
        return checkoutOverviewPage;
    }

    public void setCheckoutOverviewPage(CheckoutOverviewPage page) {
        this.checkoutOverviewPage = page;
    }

    public CheckoutCompletePage checkoutCompletePage() {
        return checkoutCompletePage;
    }

    public void setCheckoutCompletePage(CheckoutCompletePage page) {
        this.checkoutCompletePage = page;
    }
}
