package com.ore.saucedemo.tests;

import com.ore.saucedemo.base.BaseTest;
import com.ore.saucedemo.data.TestDataProvider;
import com.ore.saucedemo.model.CheckoutInfo;
import com.ore.saucedemo.pages.CartPage;
import com.ore.saucedemo.pages.CheckoutCompletePage;
import com.ore.saucedemo.pages.CheckoutInformationPage;
import com.ore.saucedemo.pages.CheckoutOverviewPage;
import com.ore.saucedemo.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("SauceDemo storefront")
@Feature("Checkout")
public class CheckoutTests extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void signIn() {
        inventory = signInAsStandardUser();
    }

    @Test(groups = {"smoke", "checkout"})
    @Story("A customer can place an order")
    @Severity(SeverityLevel.BLOCKER)
    @Description("The end-to-end happy path: sign in, add two products, check out, confirm.")
    public void customerCanCompleteAnOrder() {
        CheckoutCompletePage confirmation = inventory
                .addToCart(BACKPACK)
                .addToCart(BIKE_LIGHT)
                .openCart()
                .checkout()
                .submit(CheckoutInfo.valid())
                .finish();

        assertTrue(confirmation.isLoaded(), "Confirmation page did not load");
        assertTrue(confirmation.confirmationHeader().toLowerCase().contains("thank you"),
                "Unexpected confirmation heading: " + confirmation.confirmationHeader());
        assertTrue(confirmation.hasConfirmationImage(), "Confirmation image was missing");
        assertEquals(confirmation.header().cartCount(), 0, "Cart was not emptied once the order was placed");
    }

    @Test(groups = {"regression", "checkout"})
    @Story("The order summary is accurate")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The summary lists exactly the products in the cart and its item total "
            + "equals the sum of their prices.")
    public void orderSummaryMatchesTheCart() {
        CartPage cart = inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT).openCart();
        List<String> expectedItems = cart.itemNames();
        BigDecimal expectedSubtotal = cart.subtotal();

        CheckoutOverviewPage overview = cart.checkout().submit(CheckoutInfo.valid());

        assertTrue(overview.isLoaded(), "Order summary did not load");
        assertEquals(overview.itemNames(), expectedItems, "Summary listed different products from the cart");
        assertEquals(overview.itemTotal().compareTo(expectedSubtotal), 0,
                "Item total " + overview.itemTotal() + " did not match the cart subtotal " + expectedSubtotal);
    }

    @Test(groups = {"regression", "checkout"})
    @Story("The order summary is accurate")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The displayed total is the item total plus the tax line - the arithmetic "
            + "a customer would check on the invoice.")
    public void totalIsItemTotalPlusTax() {
        CheckoutOverviewPage overview = inventory
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .submit(CheckoutInfo.valid());

        BigDecimal expectedTotal = overview.itemTotal().add(overview.tax());

        assertEquals(overview.total().compareTo(expectedTotal), 0,
                "Total " + overview.total() + " is not item total " + overview.itemTotal()
                        + " plus tax " + overview.tax());
        assertTrue(overview.tax().compareTo(BigDecimal.ZERO) > 0, "Tax line was zero or missing");
    }

    @Test(groups = {"regression", "checkout"}, dataProvider = "incompleteCheckoutDetails",
            dataProviderClass = TestDataProvider.class)
    @Story("Incomplete details are rejected")
    @Severity(SeverityLevel.NORMAL)
    @Description("Each required field on checkout step one is enforced with its own message.")
    public void incompleteDetailsAreRejected(String firstName, String lastName, String postcode, String expectedError) {
        CheckoutInformationPage form = inventory
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .submitExpectingError(firstName, lastName, postcode);

        assertTrue(form.hasErrorMessage(), "No validation error for a missing field");
        assertTrue(form.errorMessage().contains(expectedError),
                "Expected '" + expectedError + "' but the form said: " + form.errorMessage());
    }

    @Test(groups = {"regression", "checkout"})
    @Story("Checkout can be abandoned")
    @Severity(SeverityLevel.MINOR)
    @Description("Cancelling step one returns to the cart with the products intact.")
    public void cancellingTheFormReturnsToTheCart() {
        CartPage cart = inventory.addToCart(BACKPACK).openCart().checkout().cancel();

        assertTrue(cart.isLoaded(), "Cancel did not return to the cart");
        assertTrue(cart.contains(BACKPACK), "Cancelling checkout emptied the cart");
    }

    @Test(groups = {"regression", "checkout"})
    @Story("Checkout can be abandoned")
    @Severity(SeverityLevel.MINOR)
    @Description("Cancelling the summary returns to the catalogue with the cart intact.")
    public void cancellingTheSummaryReturnsToTheCatalogue() {
        InventoryPage listing = inventory
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .submit(CheckoutInfo.valid())
                .cancel();

        assertTrue(listing.isLoaded(), "Cancel did not return to the product list");
        assertEquals(listing.header().cartCount(), 1, "Cancelling the summary discarded the cart");
    }

    @Test(groups = {"regression", "checkout"})
    @Story("A customer can place an order")
    @Severity(SeverityLevel.NORMAL)
    @Description("After an order, Back Home returns to a catalogue with an empty cart.")
    public void catalogueIsResetAfterAnOrder() {
        InventoryPage listing = inventory
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .submit(CheckoutInfo.valid())
                .finish()
                .backHome();

        assertTrue(listing.isLoaded(), "Back Home did not return to the product list");
        assertEquals(listing.header().cartCount(), 0, "Cart still had products after the order completed");
        assertEquals(listing.buttonLabelFor(BACKPACK), "Add to cart",
                "Ordered product still showed as being in the cart");
    }
}
