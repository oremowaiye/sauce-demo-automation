package com.ore.saucedemo.pages;

import com.ore.saucedemo.model.Product;
import com.ore.saucedemo.model.SortOption;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** The product listing shown straight after a successful sign-in. */
public class InventoryPage extends BasePage {

    private static final By PAGE_TITLE = By.className("title");
    private static final By INVENTORY_CONTAINER = By.id("inventory_container");
    private static final By INVENTORY_ITEM = By.className("inventory_item");
    private static final By SORT_DROPDOWN = By.className("product_sort_container");

    // Locators used relative to a single inventory item.
    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_DESCRIPTION = By.className("inventory_item_desc");
    private static final By ITEM_PRICE = By.className("inventory_item_price");
    private static final By ITEM_BUTTON = By.tagName("button");

    private final HeaderComponent header;

    public InventoryPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    @Override
    protected By uniqueLocator() {
        return INVENTORY_CONTAINER;
    }

    public HeaderComponent header() {
        return header;
    }

    public String title() {
        return textOf(PAGE_TITLE);
    }

    public int productCount() {
        return waitForAllVisible(INVENTORY_ITEM).size();
    }

    /** Every product on the page, in the order the page currently shows them. */
    public List<Product> products() {
        return waitForAllVisible(INVENTORY_ITEM).stream()
                .map(item -> new Product(
                        item.findElement(ITEM_NAME).getText().trim(),
                        item.findElement(ITEM_DESCRIPTION).getText().trim(),
                        Product.parsePrice(item.findElement(ITEM_PRICE).getText())))
                .toList();
    }

    public List<String> productNames() {
        return products().stream().map(Product::name).toList();
    }

    public List<BigDecimal> productPrices() {
        return products().stream().map(Product::price).toList();
    }

    @Step("Add '{productName}' to the cart")
    public InventoryPage addToCart(String productName) {
        itemFor(productName).findElement(ITEM_BUTTON).click();
        return this;
    }

    @Step("Remove '{productName}' from the cart")
    public InventoryPage removeFromCart(String productName) {
        itemFor(productName).findElement(ITEM_BUTTON).click();
        return this;
    }

    /** Button text doubles as state on SauceDemo: "Add to cart" or "Remove". */
    public String buttonLabelFor(String productName) {
        return itemFor(productName).findElement(ITEM_BUTTON).getText().trim();
    }

    public boolean isInCart(String productName) {
        return "Remove".equalsIgnoreCase(buttonLabelFor(productName));
    }

    @Step("Sort products by {option}")
    public InventoryPage sortBy(SortOption option) {
        selectByValue(SORT_DROPDOWN, option.value());
        return this;
    }

    public String selectedSortLabel() {
        return selectedOption(SORT_DROPDOWN);
    }

    @Step("Open the details page for '{productName}'")
    public ProductDetailsPage openProduct(String productName) {
        itemFor(productName).findElement(ITEM_NAME).click();
        return new ProductDetailsPage(driver);
    }

    public CartPage openCart() {
        return header.openCart();
    }

    private WebElement itemFor(String productName) {
        return waitForAllVisible(INVENTORY_ITEM).stream()
                .filter(item -> item.findElement(ITEM_NAME).getText().trim().equalsIgnoreCase(productName.trim()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "No product named '" + productName + "' on the inventory page. Found: " + productNames()));
    }
}
