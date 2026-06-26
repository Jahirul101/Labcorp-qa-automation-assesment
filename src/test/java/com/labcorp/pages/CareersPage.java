package com.labcorp.pages;

import com.labcorp.utils.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CareersPage extends BasePage {

    // Keep multiple locator strategies
    private final By keywordInputById = By.id("keyword-search");
    private final By keywordInputByName = By.name("keyword");
    private final By keywordInputFallback = By.cssSelector("input[placeholder*='Search'], input[type='search']");

    private final By searchBtn = By.cssSelector("button[type='submit'], button[aria-label*='Search']");
    private final By careersUrlToken = By.xpath("//body");

    public void waitForPage() {
        // Robust page readiness: URL OR any known search input visible
        wait.until(driver -> driver.getCurrentUrl().toLowerCase().contains("careers")
                || driver.getCurrentUrl().toLowerCase().contains("jobs"));

        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(keywordInputById),
                    ExpectedConditions.visibilityOfElementLocated(keywordInputByName),
                    ExpectedConditions.visibilityOfElementLocated(keywordInputFallback)
            ));
        } catch (TimeoutException e) {
            // final fallback: page loaded body present
            visible(careersUrlToken);
        }
    }

    public void search(String keyword) {
        By activeInput = resolveSearchInput();
        type(activeInput, keyword);

        try {
            click(searchBtn);
        } catch (Exception ignored) {
            // Enter fallback
            visible(activeInput).sendKeys(org.openqa.selenium.Keys.ENTER);
        }
    }

    private By resolveSearchInput() {
        if (isDisplayed(keywordInputById)) return keywordInputById;
        if (isDisplayed(keywordInputByName)) return keywordInputByName;
        return keywordInputFallback;
    }
}