package com.ore.saucedemo.data;

import com.ore.saucedemo.config.ConfigReader;
import org.testng.annotations.DataProvider;

/**
 * Test inputs kept out of the test bodies. Adding a case is one more line here,
 * instead of another copy-pasted test method.
 */
public final class TestDataProvider {

    private TestDataProvider() {
    }

    /** { username, password, expected error fragment } */
    @DataProvider(name = "invalidLogins")
    public static Object[][] invalidLogins() {
        String validUser = ConfigReader.get("user.standard");
        String validPassword = ConfigReader.password();

        return new Object[][]{
                {"", "", "Username is required"},
                {validUser, "", "Password is required"},
                {"", validPassword, "Username is required"},
                {"no_such_user", validPassword, "do not match any user"},
                {validUser, "wrong_password", "do not match any user"},
                {validUser.toUpperCase(), validPassword, "do not match any user"}
        };
    }

    /** { firstName, lastName, postcode, expected error fragment } */
    @DataProvider(name = "incompleteCheckoutDetails")
    public static Object[][] incompleteCheckoutDetails() {
        return new Object[][]{
                {"", "Mowaiye", "SE1 9SG", "First Name is required"},
                {"Ore", "", "SE1 9SG", "Last Name is required"},
                {"Ore", "Mowaiye", "", "Postal Code is required"}
        };
    }

    /** Products used by the cart and checkout tests. */
    @DataProvider(name = "products")
    public static Object[][] products() {
        return new Object[][]{
                {"Sauce Labs Backpack"},
                {"Sauce Labs Bike Light"},
                {"Sauce Labs Bolt T-Shirt"},
                {"Sauce Labs Onesie"}
        };
    }
}
