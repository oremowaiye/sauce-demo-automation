package com.ore.saucedemo.tests;

import com.ore.saucedemo.base.BaseTest;
import com.ore.saucedemo.model.Product;
import com.ore.saucedemo.model.SortOption;
import com.ore.saucedemo.pages.InventoryPage;
import com.ore.saucedemo.pages.ProductDetailsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("SauceDemo storefront")
@Feature("Product catalogue")
public class InventoryTests extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void signIn() {
        inventory = signInAsStandardUser();
    }

    @Test(groups = {"smoke", "inventory"})
    @Story("All products are listed")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The catalogue shows the full set of six products, each with a price.")
    public void allProductsAreListed() {
        List<Product> products = inventory.products();

        assertEquals(products.size(), 6, "Unexpected number of products on the inventory page");
        assertTrue(products.stream().allMatch(p -> p.price().compareTo(BigDecimal.ZERO) > 0),
                "At least one product was listed without a positive price: " + products);
        assertTrue(products.stream().noneMatch(p -> p.name().isBlank()),
                "At least one product was listed without a name");
    }

    @Test(groups = {"regression", "inventory"})
    @Story("Products can be sorted")
    @Severity(SeverityLevel.NORMAL)
    @Description("Name A-Z is the default ordering when the page first loads.")
    public void productsAreSortedByNameByDefault() {
        assertEquals(inventory.selectedSortLabel(), SortOption.NAME_A_TO_Z.label(),
                "Default sort option was not Name (A to Z)");
        assertSorted(inventory.productNames(), Comparator.naturalOrder(), "default A-Z order");
    }

    @Test(groups = {"regression", "inventory"})
    @Story("Products can be sorted")
    @Severity(SeverityLevel.NORMAL)
    @Description("Choosing Name (Z to A) reverses the alphabetical order.")
    public void productsCanBeSortedByNameDescending() {
        inventory.sortBy(SortOption.NAME_Z_TO_A);

        assertSorted(inventory.productNames(), Comparator.reverseOrder(), "Z-A order");
    }

    @Test(groups = {"smoke", "inventory"})
    @Story("Products can be sorted")
    @Severity(SeverityLevel.NORMAL)
    @Description("Choosing Price (low to high) orders the catalogue by ascending price.")
    public void productsCanBeSortedByPriceAscending() {
        inventory.sortBy(SortOption.PRICE_LOW_TO_HIGH);

        assertSorted(inventory.productPrices(), Comparator.naturalOrder(), "ascending price order");
    }

    @Test(groups = {"regression", "inventory"})
    @Story("Products can be sorted")
    @Severity(SeverityLevel.NORMAL)
    @Description("Choosing Price (high to low) orders the catalogue by descending price.")
    public void productsCanBeSortedByPriceDescending() {
        inventory.sortBy(SortOption.PRICE_HIGH_TO_LOW);

        assertSorted(inventory.productPrices(), Comparator.reverseOrder(), "descending price order");
    }

    @Test(groups = {"regression", "inventory"})
    @Story("A product has its own page")
    @Severity(SeverityLevel.NORMAL)
    @Description("The details page shows the same name, description and price as the listing.")
    public void productDetailsMatchTheListing() {
        Product listed = inventory.products().stream()
                .filter(p -> p.name().equals(BACKPACK))
                .findFirst()
                .orElseThrow();

        ProductDetailsPage details = inventory.openProduct(BACKPACK);

        assertTrue(details.isLoaded(), "Product details page did not load");
        assertEquals(details.product(), listed,
                "Details page did not match the catalogue entry for " + BACKPACK);
    }

    @Test(groups = {"smoke", "inventory"})
    @Story("A product can be added from the listing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adding a product flips its button to Remove and increments the cart badge.")
    public void addingAProductUpdatesButtonAndBadge() {
        inventory.addToCart(BACKPACK);

        assertEquals(inventory.buttonLabelFor(BACKPACK), "Remove",
                "Button did not change state after the product was added");
        assertEquals(inventory.header().cartCount(), 1, "Cart badge did not show one item");
    }

    @Test(groups = {"regression", "inventory"})
    @Story("A product can be added from its own page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Adding from the details page updates the cart, and the state survives "
            + "navigating back to the listing.")
    public void addingFromTheDetailsPagePersists() {
        ProductDetailsPage details = inventory.openProduct(BACKPACK).addToCart();

        assertEquals(details.header().cartCount(), 1, "Cart badge did not update from the details page");

        InventoryPage listing = details.backToProducts();

        assertTrue(listing.isInCart(BACKPACK), "Product did not stay in the cart after going back");
        assertEquals(listing.header().cartCount(), 1, "Cart badge changed after navigating back");
    }

    @Test(groups = {"regression", "inventory"})
    @Story("A product can be removed from the listing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Removing a product returns its button to Add to cart and clears the badge.")
    public void removingAProductClearsTheBadge() {
        inventory.addToCart(BACKPACK).removeFromCart(BACKPACK);

        assertEquals(inventory.buttonLabelFor(BACKPACK), "Add to cart",
                "Button did not return to its initial state");
        assertEquals(inventory.header().cartCount(), 0, "Cart badge was still showing after removal");
    }

    private <T> void assertSorted(List<T> actual, Comparator<? super T> expectedOrder, String description) {
        List<T> expected = actual.stream().sorted(expectedOrder).toList();
        assertEquals(actual, expected, "Products were not in " + description);
    }
}
