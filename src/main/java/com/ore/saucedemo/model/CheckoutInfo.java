package com.ore.saucedemo.model;

/** The three fields SauceDemo asks for on checkout step one. */
public record CheckoutInfo(String firstName, String lastName, String postalCode) {

    public static CheckoutInfo valid() {
        return new CheckoutInfo("Ore", "Mowaiye", "SE1 9SG");
    }
}
