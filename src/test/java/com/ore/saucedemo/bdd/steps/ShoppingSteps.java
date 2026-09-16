package com.ore.saucedemo.bdd.steps;

import com.ore.saucedemo.bdd.hooks.ScenarioContext;
import com.ore.saucedemo.model.CheckoutInfo;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ShoppingSteps {

    private final ScenarioContext context;

    public ShoppingSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the shopper adds {string} to the cart")
    public void theShopperAddsToTheCart(String productName) {
        context.inventoryPage().addToCart(productName);
    }

    @When("the shopper opens the cart")
    public void theShopperOpensTheCart() {
        context.setCartPage(context.inventoryPage().openCart());
    }

    @Then("the cart badge shows {int}")
    public void theCartBadgeShows(int expectedCount) {
        assertEquals(context.inventoryPage().header().cartCount(), expectedCount,
                "Cart badge showed the wrong number of items");
    }

    @Then("the cart contains {string}")
    public void theCartContains(String productName) {
        assertTrue(context.cartPage().contains(productName),
                "Cart did not contain " + productName + ". It held: " + context.cartPage().itemNames());
    }

    @When("the shopper removes {string} from the cart")
    public void theShopperRemovesFromTheCart(String productName) {
        context.cartPage().removeItem(productName);
    }

    @Then("the cart is empty")
    public void theCartIsEmpty() {
        assertTrue(context.cartPage().isEmpty(), "Cart still held: " + context.cartPage().itemNames());
    }

    @When("the shopper starts checkout")
    public void theShopperStartsCheckout() {
        context.setCheckoutInformationPage(context.cartPage().checkout());
    }

    @And("the shopper provides valid delivery details")
    public void theShopperProvidesValidDeliveryDetails() {
        context.setCheckoutOverviewPage(context.checkoutInformationPage().submit(CheckoutInfo.valid()));
    }

    @When("the shopper submits delivery details {string}, {string} and {string}")
    public void theShopperSubmitsDeliveryDetails(String firstName, String lastName, String postcode) {
        context.checkoutInformationPage().submitExpectingError(firstName, lastName, postcode);
    }

    @Then("the checkout form shows the error {string}")
    public void theCheckoutFormShowsTheError(String expectedFragment) {
        assertTrue(context.checkoutInformationPage().hasErrorMessage(), "No validation error was shown");
        assertTrue(context.checkoutInformationPage().errorMessage().contains(expectedFragment),
                "Expected '" + expectedFragment + "' but the form said: "
                        + context.checkoutInformationPage().errorMessage());
    }

    @Then("the order total is the item total plus tax")
    public void theOrderTotalIsTheItemTotalPlusTax() {
        BigDecimal expected = context.checkoutOverviewPage().itemTotal().add(context.checkoutOverviewPage().tax());
        assertEquals(context.checkoutOverviewPage().total().compareTo(expected), 0,
                "Total " + context.checkoutOverviewPage().total() + " did not equal item total plus tax");
    }

    @When("the shopper confirms the order")
    public void theShopperConfirmsTheOrder() {
        context.setCheckoutCompletePage(context.checkoutOverviewPage().finish());
    }

    @Then("the order is confirmed")
    public void theOrderIsConfirmed() {
        assertTrue(context.checkoutCompletePage().isLoaded(), "Confirmation page did not load");
        assertTrue(context.checkoutCompletePage().confirmationHeader().toLowerCase().contains("thank you"),
                "Unexpected confirmation heading: " + context.checkoutCompletePage().confirmationHeader());
    }

    @Then("the cart is emptied")
    public void theCartIsEmptied() {
        assertEquals(context.checkoutCompletePage().header().cartCount(), 0,
                "The cart was not emptied after the order");
    }
}
