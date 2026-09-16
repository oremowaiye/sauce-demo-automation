package com.ore.saucedemo.model;

import com.ore.saucedemo.config.ConfigReader;

/**
 * The SauceDemo demo accounts. Usernames come from configuration so the suite never
 * hard-codes credentials in test classes.
 */
public enum TestUser {

    STANDARD("user.standard"),
    LOCKED_OUT("user.locked"),
    PROBLEM("user.problem"),
    PERFORMANCE_GLITCH("user.glitch");

    private final String configKey;

    TestUser(String configKey) {
        this.configKey = configKey;
    }

    public String username() {
        return ConfigReader.get(configKey);
    }

    public String password() {
        return ConfigReader.password();
    }
}
