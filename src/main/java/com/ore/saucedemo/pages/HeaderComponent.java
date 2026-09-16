package com.ore.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * The banner shared by every signed-in page: burger menu, cart link and cart badge.
 * Modelled as its own component so the cart-count logic is written once, not on
 * every page that shows the header.
 */
public class HeaderComponent extends BasePage {

    private static final By BURGER_BUTTON = By.id("react-burger-menu-btn");
    private static final By CLOSE_MENU_BUTTON = By.id("react-burger-cross-btn");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");
    private static final By RESET_APP_LINK = By.id("reset_sidebar_link");
    private static final By ALL_ITEMS_LINK = By.id("inventory_sidebar_link");
    private static final By CART_LINK = By.className("shopping_cart_link");
    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By APP_LOGO = By.className("app_logo");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By uniqueLocator() {
        return APP_LOGO;
    }

    /** Number shown on the cart icon; zero when the badge is not rendered at all. */
    public int cartCount() {
        if (!isPresent(CART_BADGE)) {
            return 0;
        }
        return Integer.parseInt(textOf(CART_BADGE));
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }

    public LoginPage logout() {
        openMenu();
        click(LOGOUT_LINK);
        return new LoginPage(driver);
    }

    public HeaderComponent resetAppState() {
        openMenu();
        click(RESET_APP_LINK);
        closeMenu();
        return this;
    }

    public InventoryPage goToAllItems() {
        openMenu();
        click(ALL_ITEMS_LINK);
        return new InventoryPage(driver);
    }

    private void openMenu() {
        click(BURGER_BUTTON);
        waitForVisible(LOGOUT_LINK);
    }

    private void closeMenu() {
        click(CLOSE_MENU_BUTTON);
    }
}
