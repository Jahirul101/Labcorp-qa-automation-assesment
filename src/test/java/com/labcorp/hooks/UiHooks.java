package com.labcorp.hooks;

import com.labcorp.drivers.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UiHooks {
    
    private static final Logger logger = LoggerFactory.getLogger(UiHooks.class);
    private final DriverFactory driverFactory;
    private WebDriver driver;
    
    public UiHooks() {
        this.driverFactory = DriverFactory.getInstance();
    }
    
    @Before("@UI")
    public void setUp() {
        logger.info("Setting up WebDriver for UI test");
        driver = driverFactory.getDriver();
    }
    
    @After("@UI")
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "screenshot_" + scenario.getName());
            }
            logger.info("Scenario '{}' completed with status: {}", scenario.getName(), scenario.getStatus());
        } catch (Exception e) {
            logger.error("Error during teardown: {}", e.getMessage());
        } finally {
            driverFactory.quitDriver();
        }
    }
}
