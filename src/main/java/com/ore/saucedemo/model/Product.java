package com.ore.saucedemo.model;

import java.math.BigDecimal;

/**
 * A product as it appears on the inventory or cart page.
 *
 * @param name        product title, e.g. "Sauce Labs Backpack"
 * @param description marketing copy shown under the title
 * @param price       price without the currency symbol
 */
public record Product(String name, String description, BigDecimal price) {

    /** Parses a displayed price such as "$29.99" into a comparable value. */
    public static BigDecimal parsePrice(String displayedPrice) {
        return new BigDecimal(displayedPrice.replace("$", "").replace(",", "").trim());
    }
}
