package com.labcorp.pages;

import com.labcorp.drivers.DriverFactory;
import com.labcorp.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public abstract class BasePage {
    
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    
    protected BasePage() {
        WebDriver driverInstance = DriverFactory.getInstance().getDriver();
        if (driverInstance == null) {
            throw new IllegalStateException("WebDriver not initialized. Call DriverFactory.getInstance().getDriver() first");
        }
        this.driver = driverInstance;
        this.waitUtils = new WaitUtils(driver);
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }
    
    protected WebElement findElement(By locator) {
        return waitUtils.waitForVisibility(locator);
    }
    
    protected WebElement findElement(By locator, int timeoutSeconds) {
        return waitUtils.waitForVisibility(locator, timeoutSeconds);
    }
    
    protected WebElement findElementById(String id) {
        return findElement(By.id(id));
    }
    
    protected WebElement findElementByCss(String cssSelector) {
        return findElement(By.cssSelector(cssSelector));
    }
    
    protected WebElement findElementByXPath(String xpath) {
        return findElement(By.xpath(xpath));
    }
    
    protected List<WebElement> findElements(By locator) {
        return waitUtils.waitForAllVisibility(locator);
    }
    
    protected void click(By locator) {
        waitUtils.waitForClickability(locator).click();
    }
    
    protected void click(WebElement element) {
        waitUtils.waitForClickability(element).click();
    }
    
    protected void clickWithJavascript(WebElement element) {
        jsExecutor.executeScript("arguments[0].click();", element);
    }
    
    protected void clickWithJavascript(By locator) {
        WebElement element = findElement(locator);
        jsExecutor.executeScript("arguments[0].click();", element);
    }
    
    protected void sendKeys(By locator, String text) {
        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(text);
    }
    
    protected String getText(By locator) {
        return findElement(locator).getText().trim();
    }
    
    protected void scrollToElement(By locator) {
        WebElement element = findElement(locator);
        jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }
    
    protected void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }
    
    protected void waitForPageLoad() {
        waitUtils.waitForPageToLoad();
    }
    
    protected boolean isElementDisplayed(By locator) {
        try {
            return findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    protected boolean isElementPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    protected boolean isElementPresent(By locator, int timeoutSeconds) {
        try {
            return waitUtils.waitForPresence(locator, timeoutSeconds) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    protected boolean containsText(String text) {
        return driver.getPageSource().contains(text);
    }
    
    protected void switchToNewWindow() {
        String currentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(currentWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }
        waitForPageLoad();
    }
    
    protected void refreshPage() {
        driver.navigate().refresh();
        waitForPageLoad();
    }
}
