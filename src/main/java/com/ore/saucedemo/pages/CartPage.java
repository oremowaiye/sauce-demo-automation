package com.ore.saucedemo.pages;

import com.ore.saucedemo.model.Product;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** The basket: what has been added, and the way through to checkout. */
public class CartPage extends BasePage {

    private static final By CART_LIST = By.className("cart_list");
    private static final By CART_ITEM = By.className("cart_item");
    private static final By PAGE_TITLE = By.className("title");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");

    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_DESCRIPTION = By.className("inventory_item_desc");
    private static final By ITEM_PRICE = By.className("inventory_item_price");
    private static final By ITEM_QUANTITY = By.className("cart_quantity");
    private static final By ITEM_REMOVE_BUTTON = By.tagName("button");

    private final HeaderComponent header;

    public CartPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    @Override
    protected By uniqueLocator() {
        return CART_LIST;
    }

    public HeaderComponent header() {
        return header;
    }

    public String title() {
        return textOf(PAGE_TITLE);
    }

    public int itemCount() {
        return driver.findElements(CART_ITEM).size();
    }

    public boolean isEmpty() {
        return itemCount() == 0;
    }

    public List<Product> items() {
        return driver.findElements(CART_ITEM).stream()
                .map(item -> new Product(
                        item.findElement(ITEM_NAME).getText().trim(),
                        item.findElement(ITEM_DESCRIPTION).getText().trim(),
                        Product.parsePrice(item.findElement(ITEM_PRICE).getText())))
                .toList();
    }

    public List<String> itemNames() {
        return items().stream().map(Product::name).toList();
    }

    public boolean contains(String productName) {
        return itemNames().stream().anyMatch(name -> name.equalsIgnoreCase(productName.trim()));
    }

    public int quantityOf(String productName) {
        return Integer.parseInt(itemFor(productName).findElement(ITEM_QUANTITY).getText().trim());
    }

    /** Sum of the line prices, used to check the totals shown at checkout. */
    public BigDecimal subtotal() {
        return items().stream().map(Product::price).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Step("Remove '{productName}' from the cart")
    public CartPage removeItem(String productName) {
        itemFor(productName).findElement(ITEM_REMOVE_BUTTON).click();
        return this;
    }

    @Step("Start checkout")
    public CheckoutInformationPage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutInformationPage(driver);
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        click(CONTINUE_SHOPPING_BUTTON);
        return new InventoryPage(driver);
    }

    private WebElement itemFor(String productName) {
        return driver.findElements(CART_ITEM).stream()
                .filter(item -> item.findElement(ITEM_NAME).getText().trim().equalsIgnoreCase(productName.trim()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "'" + productName + "' is not in the cart. Cart holds: " + itemNames()));
    }
}
