package com.labcorp.hooks;

import com.labcorp.utils.ConfigManager;
import com.labcorp.utils.DriverManager;
import com.labcorp.utils.ScreenshotUtil;
import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {
    private static final Logger log = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        DriverManager.initializeDriver(
                ConfigManager.get("browser"),
                Long.parseLong(ConfigManager.get("explicitWaitSeconds"))
        );
        log.info("Scenario started: {}", scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        // screenshot after each step for richer reporting
        try {
            String path = ScreenshotUtil.capture("step_" + scenario.getName());
            byte[] png = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
            scenario.attach(png, "image/png", "Step Screenshot");
        } catch (Exception e) {
            log.warn("Step screenshot failed: {}", e.getMessage());
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                String path = ScreenshotUtil.capture("failed_" + scenario.getName());
                byte[] png = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
                scenario.attach(png, "image/png", "Failure Screenshot");
                log.error("Scenario failed: {} | screenshot: {}", scenario.getName(), path);
            } else {
                log.info("Scenario passed: {}", scenario.getName());
            }
        } catch (Exception e) {
            log.warn("Failure screenshot attach failed: {}", e.getMessage());
        } finally {
            DriverManager.quitDriver();
        }
    }
}