package com.ore.saucedemo.pages;

import com.ore.saucedemo.model.Product;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.List;

/** Checkout step two: the order summary, where the money is checked. */
public class CheckoutOverviewPage extends BasePage {

    private static final By SUMMARY_INFO = By.className("summary_info");
    private static final By CART_ITEM = By.className("cart_item");
    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_DESCRIPTION = By.className("inventory_item_desc");
    private static final By ITEM_PRICE = By.className("inventory_item_price");
    private static final By SUBTOTAL_LABEL = By.className("summary_subtotal_label");
    private static final By TAX_LABEL = By.className("summary_tax_label");
    private static final By TOTAL_LABEL = By.className("summary_total_label");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By CANCEL_BUTTON = By.id("cancel");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By uniqueLocator() {
        return SUMMARY_INFO;
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

    /** "Item total: $29.99" -> 29.99 */
    public BigDecimal itemTotal() {
        return amountFrom(textOf(SUBTOTAL_LABEL));
    }

    /** "Tax: $2.40" -> 2.40 */
    public BigDecimal tax() {
        return amountFrom(textOf(TAX_LABEL));
    }

    /** "Total: $32.39" -> 32.39 */
    public BigDecimal total() {
        return amountFrom(textOf(TOTAL_LABEL));
    }

    @Step("Finish the order")
    public CheckoutCompletePage finish() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver);
    }

    @Step("Cancel the order")
    public InventoryPage cancel() {
        click(CANCEL_BUTTON);
        return new InventoryPage(driver);
    }

    private BigDecimal amountFrom(String label) {
        return Product.parsePrice(label.substring(label.indexOf('$')));
    }
}
