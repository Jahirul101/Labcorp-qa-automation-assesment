package com.labcorp.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class WaitUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    
    private final WebDriver driver;
    private final int defaultTimeoutSeconds;
    private final int pollingIntervalMillis;
    
    public WaitUtils(WebDriver driver) {
        this(driver, 30, 500);
    }
    
    public WaitUtils(WebDriver driver, int timeoutSeconds) {
        this(driver, timeoutSeconds, 500);
    }
    
    public WaitUtils(WebDriver driver, int timeoutSeconds, int pollingIntervalMillis) {
        this.driver = driver;
        this.defaultTimeoutSeconds = timeoutSeconds;
        this.pollingIntervalMillis = pollingIntervalMillis;
    }
    
    public WebElement waitForVisibility(By locator) {
        return waitForVisibility(locator, defaultTimeoutSeconds);
    }
    
    public WebElement waitForVisibility(By locator, int timeoutSeconds) {
        return getFluentWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    public WebElement waitForClickability(By locator) {
        return waitForClickability(locator, defaultTimeoutSeconds);
    }
    
    public WebElement waitForClickability(By locator, int timeoutSeconds) {
        return getFluentWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    public WebElement waitForClickability(WebElement element) {
        return waitForClickability(element, defaultTimeoutSeconds);
    }
    
    public WebElement waitForClickability(WebElement element, int timeoutSeconds) {
        return getFluentWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(element));
    }
    
    public WebElement waitForPresence(By locator) {
        return waitForPresence(locator, defaultTimeoutSeconds);
    }
    
    public WebElement waitForPresence(By locator, int timeoutSeconds) {
        return getFluentWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    public List<WebElement> waitForAllVisibility(By locator) {
        return waitForAllVisibility(locator, defaultTimeoutSeconds);
    }
    
    public List<WebElement> waitForAllVisibility(By locator, int timeoutSeconds) {
        return getFluentWait(timeoutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
    
    public boolean waitForInvisibility(By locator) {
        return waitForInvisibility(locator, defaultTimeoutSeconds);
    }
    
    public boolean waitForInvisibility(By locator, int timeoutSeconds) {
        return getFluentWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    public boolean waitForTextToContain(By locator, String expectedText) {
        return waitForTextToContain(locator, expectedText, defaultTimeoutSeconds);
    }
    
    public boolean waitForTextToContain(By locator, String expectedText, int timeoutSeconds) {
        return getFluentWait(timeoutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }
    
    public void waitForPageToLoad() {
        waitForPageToLoad(defaultTimeoutSeconds);
    }
    
    public void waitForPageToLoad(int timeoutSeconds) {
        getFluentWait(timeoutSeconds).until((Function<WebDriver, Boolean>) driver ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }
    
    public void waitForAjaxComplete() {
        waitForAjaxComplete(defaultTimeoutSeconds);
    }
    
    public void waitForAjaxComplete(int timeoutSeconds) {
        try {
            getFluentWait(timeoutSeconds).until((Function<WebDriver, Boolean>) driver ->
                    (Boolean) ((JavascriptExecutor) driver).executeScript("return (window.jQuery || {active:0}).active === 0"));
        } catch (TimeoutException e) {
            logger.warn("AJAX complete timeout, continuing...");
        }
    }
    
    public void waitForElementToBeClickableAndClick(By locator) {
        waitForClickability(locator).click();
    }
    
    public void waitForElementToBeClickableAndClick(By locator, int timeoutSeconds) {
        waitForClickability(locator, timeoutSeconds).click();
    }
    
    public boolean waitUntil(ExpectedCondition<Boolean> condition) {
        return waitUntil(condition, defaultTimeoutSeconds);
    }
    
    public boolean waitUntil(ExpectedCondition<Boolean> condition, int timeoutSeconds) {
        try {
            return getFluentWait(timeoutSeconds).until(condition);
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    private FluentWait<WebDriver> getFluentWait(int timeoutSeconds) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingIntervalMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementNotInteractableException.class)
                .ignoring(TimeoutException.class)
                .withMessage("Element condition not satisfied within " + timeoutSeconds + " seconds");
    }
    
    public WebElement findElementWithRetry(By locator, int retries) {
        return findElementWithRetry(locator, retries, defaultTimeoutSeconds);
    }
    
    public WebElement findElementWithRetry(By locator, int retries, int timeoutSeconds) {
        for (int i = 0; i < retries; i++) {
            try {
                return waitForVisibility(locator, timeoutSeconds);
            } catch (Exception e) {
                if (i == retries - 1) {
                    throw new RuntimeException("Failed to find element after " + retries + " attempts: " + locator, e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null;
    }
}
