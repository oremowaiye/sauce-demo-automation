package com.ore.saucedemo.tests;

import com.ore.saucedemo.base.BaseTest;
import com.ore.saucedemo.data.TestDataProvider;
import com.ore.saucedemo.pages.CartPage;
import com.ore.saucedemo.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Epic("SauceDemo storefront")
@Feature("Shopping cart")
public class CartTests extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void signIn() {
        inventory = signInAsStandardUser();
    }

    @Test(groups = {"smoke", "cart"}, dataProvider = "products", dataProviderClass = TestDataProvider.class)
    @Story("Products reach the cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Any product added from the listing appears in the cart with quantity one.")
    public void addedProductAppearsInTheCart(String productName) {
        CartPage cart = inventory.addToCart(productName).openCart();

        assertTrue(cart.isLoaded(), "Cart page did not load");
        assertEquals(cart.title(), "Your Cart", "Unexpected cart page heading");
        assertTrue(cart.contains(productName), "Cart did not contain " + productName + ": " + cart.itemNames());
        assertEquals(cart.quantityOf(productName), 1, "Unexpected quantity for " + productName);
    }

    @Test(groups = {"regression", "cart"})
    @Story("Products reach the cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("The badge counts every product added, and the cart lists them all.")
    public void badgeCountsEveryAddedProduct() {
        inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT);

        assertEquals(inventory.header().cartCount(), 2, "Cart badge did not count both products");

        CartPage cart = inventory.openCart();

        assertEquals(cart.itemCount(), 2, "Cart did not list both products");
        assertTrue(cart.itemNames().containsAll(java.util.List.of(BACKPACK, BIKE_LIGHT)),
                "Cart was missing one of the added products: " + cart.itemNames());
    }

    @Test(groups = {"regression", "cart"})
    @Story("Products can be taken out of the cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Removing a line from the cart page updates both the list and the badge.")
    public void removingFromTheCartUpdatesTheBadge() {
        CartPage cart = inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT).openCart();

        cart.removeItem(BACKPACK);

        assertFalse(cart.contains(BACKPACK), "Removed product was still listed in the cart");
        assertEquals(cart.itemCount(), 1, "Cart did not shrink to one item");
        assertEquals(cart.header().cartCount(), 1, "Cart badge did not follow the removal");
    }

    @Test(groups = {"regression", "cart"})
    @Story("Products can be taken out of the cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Removing the last line leaves an empty cart and no badge at all.")
    public void emptyingTheCartRemovesTheBadge() {
        CartPage cart = inventory.addToCart(BACKPACK).openCart().removeItem(BACKPACK);

        assertTrue(cart.isEmpty(), "Cart was not empty after removing the only product");
        assertEquals(cart.header().cartCount(), 0, "Cart badge was still rendered for an empty cart");
    }

    @Test(groups = {"regression", "cart"})
    @Story("The cart survives navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Continue shopping returns to the listing without discarding the cart.")
    public void continueShoppingKeepsTheCart() {
        InventoryPage listing = inventory.addToCart(BACKPACK).openCart().continueShopping();

        assertTrue(listing.isLoaded(), "Continue shopping did not return to the product list");
        assertEquals(listing.header().cartCount(), 1, "Cart was lost on the way back to the listing");
        assertTrue(listing.isInCart(BACKPACK), "Product no longer showed as added");
    }

    @Test(groups = {"regression", "cart"})
    @Story("The cart survives navigation")
    @Severity(SeverityLevel.MINOR)
    @Description("Reset App State from the burger menu empties the cart.")
    public void resetAppStateEmptiesTheCart() {
        inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT);

        inventory.header().resetAppState();

        assertEquals(inventory.header().cartCount(), 0, "Reset App State left products in the cart");
    }
}
