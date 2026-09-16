package com.ore.saucedemo.bdd.steps;

import com.ore.saucedemo.bdd.hooks.ScenarioContext;
import com.ore.saucedemo.driver.DriverFactory;
import com.ore.saucedemo.model.TestUser;
import com.ore.saucedemo.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginSteps {

    private final ScenarioContext context;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the shopper is on the login page")
    public void theShopperIsOnTheLoginPage() {
        context.setLoginPage(new LoginPage(DriverFactory.getDriver()).open());
    }

    @When("the shopper signs in as the standard user")
    public void theShopperSignsInAsTheStandardUser() {
        context.setInventoryPage(context.loginPage().loginAs(TestUser.STANDARD));
    }

    @When("the shopper signs in as the locked out user")
    public void theShopperSignsInAsTheLockedOutUser() {
        context.loginPage().submitCredentials(
                TestUser.LOCKED_OUT.username(), TestUser.LOCKED_OUT.password());
    }

    @When("the shopper signs in with username {string} and password {string}")
    public void theShopperSignsInWith(String username, String password) {
        context.loginPage().submitCredentials(username, password);
    }

    @Then("the product list is shown")
    public void theProductListIsShown() {
        assertTrue(context.inventoryPage().isLoaded(), "The product list did not load");
        assertEquals(context.inventoryPage().title(), "Products", "Unexpected page heading");
    }

    @Then("the shopper sees the error {string}")
    public void theShopperSeesTheError(String expectedFragment) {
        assertTrue(context.loginPage().hasErrorMessage(), "No error message was shown");
        assertTrue(context.loginPage().errorMessage().contains(expectedFragment),
                "Expected the error to mention '" + expectedFragment
                        + "' but it read: " + context.loginPage().errorMessage());
    }
}
