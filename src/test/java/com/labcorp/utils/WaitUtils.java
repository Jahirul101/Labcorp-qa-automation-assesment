package com.labcorp.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class WaitUtils {
    
    private final WebDriver driver;
    private final int timeoutSeconds;
    private final int pollingIntervalMillis;
    
    public WaitUtils(WebDriver driver) {
        this(driver, 30, 500);
    }
    
    public WaitUtils(WebDriver driver, int timeoutSeconds) {
        this(driver, timeoutSeconds, 500);
    }
    
    public WaitUtils(WebDriver driver, int timeoutSeconds, int pollingIntervalMillis) {
        this.driver = driver;
        this.timeoutSeconds = timeoutSeconds;
        this.pollingIntervalMillis = pollingIntervalMillis;
    }
    
    public WebElement waitForVisibility(By locator) {
        return getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    public WebElement waitForClickability(By locator) {
        return getFluentWait().until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    public WebElement waitForPresence(By locator) {
        return getFluentWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    public List<WebElement> waitForAllVisibility(By locator) {
        return getFluentWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
    
    public boolean waitForInvisibility(By locator) {
        return getFluentWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    public boolean waitForTextToContain(By locator, String expectedText) {
        return getFluentWait().until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }
    
    public void waitForPageToLoad() {
        getFluentWait().until((Function<WebDriver, Boolean>) driver ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }
    
    public void waitForAjaxComplete() {
        getFluentWait().until((Function<WebDriver, Boolean>) driver ->
                (Boolean) ((JavascriptExecutor) driver).executeScript("return (window.jQuery || {active:0}).active === 0"));
    }
    
    private FluentWait<WebDriver> getFluentWait() {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingIntervalMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementNotInteractableException.class)
                .ignoring(TimeoutException.class)
                .withMessage("Element condition not satisfied within timeout");
    }
    
    public WebElement findElementWithRetry(By locator, int retries) {
        for (int i = 0; i < retries; i++) {
            try {
                return waitForVisibility(locator);
            } catch (Exception e) {
                if (i == retries - 1) {
                    throw new RuntimeException("Failed to find element after " + retries + " attempts: " + locator, e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }
        return null;
    }
}
