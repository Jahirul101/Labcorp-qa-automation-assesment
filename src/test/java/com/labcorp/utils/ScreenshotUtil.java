package com.labcorp.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ScreenshotUtil {
    private ScreenshotUtil(){}

    public static String capture(String namePrefix) {
        try {
            String safeName = namePrefix.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path dir = Paths.get("target", "extent-report", "screenshots");
            Files.createDirectories(dir);

            File src = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            Path target = dir.resolve(System.currentTimeMillis() + "_" + safeName + ".png");
            Files.copy(src.toPath(), target);

            return target.toString().replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("Failed to capture screenshot", e);
        }
    }
}