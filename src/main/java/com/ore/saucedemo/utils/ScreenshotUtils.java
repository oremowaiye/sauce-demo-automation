package com.ore.saucedemo.utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Captures PNGs on failure: one on disk for quick inspection, one attached to Allure. */
public final class ScreenshotUtils {

    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    public static void capture(WebDriver driver, String testName) {
        if (driver == null) {
            return;
        }
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(testName, new ByteArrayInputStream(png));
        writeToDisk(png, testName);
    }

    private static void writeToDisk(byte[] png, String testName) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String fileName = testName.replaceAll("[^A-Za-z0-9._-]", "_")
                    + "-" + LocalDateTime.now().format(STAMP) + ".png";
            Files.write(SCREENSHOT_DIR.resolve(fileName), png);
        } catch (IOException e) {
            // A screenshot we cannot save must not hide the real failure.
            System.err.println("Could not write screenshot: " + e.getMessage());
        }
    }
}
