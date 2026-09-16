package com.ore.saucedemo.pages;

import com.ore.saucedemo.model.Product;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;

/** The single-product page reached by clicking a product title. */
public class ProductDetailsPage extends BasePage {

    private static final By CONTAINER = By.className("inventory_details");
    private static final By NAME = By.className("inventory_details_name");
    private static final By DESCRIPTION = By.className("inventory_details_desc");
    private static final By PRICE = By.className("inventory_details_price");
    private static final By ACTION_BUTTON = By.cssSelector(".inventory_details_desc_container button");
    private static final By BACK_BUTTON = By.id("back-to-products");

    private final HeaderComponent header;

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    @Override
    protected By uniqueLocator() {
        return CONTAINER;
    }

    public HeaderComponent header() {
        return header;
    }

    public String name() {
        return textOf(NAME);
    }

    public String description() {
        return textOf(DESCRIPTION);
    }

    public BigDecimal price() {
        return Product.parsePrice(textOf(PRICE));
    }

    public Product product() {
        return new Product(name(), description(), price());
    }

    public String buttonLabel() {
        return textOf(ACTION_BUTTON);
    }

    @Step("Add the displayed product to the cart")
    public ProductDetailsPage addToCart() {
        click(ACTION_BUTTON);
        return this;
    }

    @Step("Go back to the product list")
    public InventoryPage backToProducts() {
        click(BACK_BUTTON);
        return new InventoryPage(driver);
    }
}
