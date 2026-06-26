package com.labcorp.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public final class DriverManager {
    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<Long> EXPLICIT_WAIT = new ThreadLocal<>();

    private DriverManager() {}

    public static void initializeDriver(String browser, long explicitWaitSeconds) {
        if (!"chrome".equalsIgnoreCase(browser)) {
            throw new UnsupportedOperationException("Only Chrome is supported.");
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        DRIVER.set(driver);
        EXPLICIT_WAIT.set(explicitWaitSeconds);

        log.info("Chrome driver initialized with explicit wait {} sec", explicitWaitSeconds);
    }

    public static WebDriver getDriver() {
        WebDriver d = DRIVER.get();
        if (d == null) {
            throw new IllegalStateException("Driver is not initialized");
        }
        return d;
    }

    public static long getExplicitWaitSeconds() {
        Long timeout = EXPLICIT_WAIT.get();
        return timeout == null ? 20L : timeout;
    }

    public static void quitDriver() {
        WebDriver d = DRIVER.get();
        if (d != null) {
            d.quit();
            DRIVER.remove();
            EXPLICIT_WAIT.remove();
            log.info("Driver quit successfully");
        }
    }
}