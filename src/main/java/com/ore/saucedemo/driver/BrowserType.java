package com.ore.saucedemo.driver;

import com.ore.saucedemo.exceptions.FrameworkException;

import java.util.Arrays;
import java.util.Locale;

/** Browsers the framework knows how to start. */
public enum BrowserType {

    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        return Arrays.stream(values())
                .filter(browser -> browser.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new FrameworkException(
                        "Unsupported browser '" + value + "'. Supported: "
                                + Arrays.toString(values()).toLowerCase(Locale.ROOT)));
    }
}
