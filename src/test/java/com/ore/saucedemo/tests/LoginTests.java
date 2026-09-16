package com.ore.saucedemo.tests;

import com.ore.saucedemo.base.BaseTest;
import com.ore.saucedemo.config.ConfigReader;
import com.ore.saucedemo.data.TestDataProvider;
import com.ore.saucedemo.model.TestUser;
import com.ore.saucedemo.pages.InventoryPage;
import com.ore.saucedemo.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Epic("SauceDemo storefront")
@Feature("Authentication")
public class LoginTests extends BaseTest {

    @Test(groups = {"smoke", "login"})
    @Story("A valid user can sign in")
    @Severity(SeverityLevel.BLOCKER)
    @Description("The standard account signs in and lands on the product list.")
    public void standardUserCanSignIn() {
        InventoryPage inventory = loginPage.loginAs(TestUser.STANDARD);

        assertTrue(inventory.isLoaded(), "Inventory page did not load after a valid sign-in");
        assertEquals(inventory.title(), "Products", "Unexpected page heading after sign-in");
        assertTrue(inventory.currentUrl().contains("inventory.html"),
                "Expected to be on the inventory URL but was: " + inventory.currentUrl());
    }

    @Test(groups = {"regression", "login"}, dataProvider = "invalidLogins", dataProviderClass = TestDataProvider.class)
    @Story("Bad credentials are rejected")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Every invalid combination is refused with a message that explains why.")
    public void invalidCredentialsAreRejected(String username, String password, String expectedError) {
        loginPage.submitCredentials(username, password);

        assertTrue(loginPage.hasErrorMessage(),
                "No error shown for username='" + username + "', password='" + password + "'");
        assertTrue(loginPage.errorMessage().contains(expectedError),
                "Expected the error to mention '" + expectedError + "' but it read: " + loginPage.errorMessage());
        assertTrue(loginPage.currentUrl().contains("saucedemo.com"),
                "A rejected sign-in should leave the user on the login page");
    }

    @Test(groups = {"smoke", "login"})
    @Story("A locked account cannot sign in")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The locked-out account is refused even with the correct password.")
    public void lockedOutUserCannotSignIn() {
        loginPage.submitCredentials(TestUser.LOCKED_OUT.username(), ConfigReader.password());

        assertTrue(loginPage.hasErrorMessage(), "Locked-out user was not shown an error");
        assertTrue(loginPage.errorMessage().contains("locked out"),
                "Expected a lock-out message but got: " + loginPage.errorMessage());
    }

    @Test(groups = {"regression", "login"})
    @Story("Bad credentials are rejected")
    @Severity(SeverityLevel.MINOR)
    @Description("The error banner can be dismissed with its close button.")
    public void errorBannerCanBeDismissed() {
        loginPage.submitCredentials("no_such_user", "no_such_password");
        assertTrue(loginPage.hasErrorMessage(), "Precondition failed: no error banner to dismiss");

        loginPage.dismissError();

        assertFalse(loginPage.hasErrorMessage(), "Error banner was still visible after being dismissed");
    }

    @Test(groups = {"regression", "login"})
    @Story("A signed-in user can sign out")
    @Severity(SeverityLevel.NORMAL)
    @Description("Signing out returns the user to the login screen.")
    public void userCanSignOut() {
        LoginPage afterLogout = signInAsStandardUser().header().logout();

        assertTrue(afterLogout.isLoaded(), "Sign-out did not return to the login page");
        assertTrue(afterLogout.currentUrl().endsWith("/"),
                "Expected the site root after sign-out but was: " + afterLogout.currentUrl());
    }

    @Test(groups = {"regression", "login", "security"})
    @Story("Protected pages require a session")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Requesting the inventory URL directly, with no session, is refused "
            + "rather than served - a basic broken-access-control check.")
    public void inventoryCannotBeReachedWithoutSigningIn() {
        driver.get(ConfigReader.baseUrl() + "/inventory.html");

        assertTrue(loginPage.hasErrorMessage(), "Direct access to the inventory page was not refused");
        assertTrue(loginPage.errorMessage().contains("when you are logged in"),
                "Expected an authentication error but got: " + loginPage.errorMessage());
    }
}
