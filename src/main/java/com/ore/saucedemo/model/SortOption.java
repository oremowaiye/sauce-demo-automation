package com.ore.saucedemo.model;

/** The four orderings offered by the inventory page's sort dropdown. */
public enum SortOption {

    NAME_A_TO_Z("az", "Name (A to Z)"),
    NAME_Z_TO_A("za", "Name (Z to A)"),
    PRICE_LOW_TO_HIGH("lohi", "Price (low to high)"),
    PRICE_HIGH_TO_LOW("hilo", "Price (high to low)");

    private final String value;
    private final String label;

    SortOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static SortOption fromLabel(String label) {
        for (SortOption option : values()) {
            if (option.label.equalsIgnoreCase(label.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Unknown sort option: " + label);
    }
}
