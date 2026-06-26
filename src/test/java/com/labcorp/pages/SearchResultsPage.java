package com.labcorp.pages;

import com.labcorp.utils.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class SearchResultsPage extends BasePage {

    private final By noResults = By.xpath(
            "//*[contains(translate(.,'NO RESULTS','no results'),'no results') " +
            "or contains(translate(.,'NO MATCHING JOBS','no matching jobs'),'no matching jobs') " +
            "or contains(translate(.,'0 RESULTS','0 results'),'0 results')]"
    );

    private final By jobLinks = By.xpath(
            "//a[@href and (" +
            "contains(@href,'/job/') or contains(@href,'/jobs/') or contains(@href,'job-details') or contains(@href,'jobdetail')" +
            ")]"
    );

    public boolean hasNoResults() {
        return !driver.findElements(noResults).isEmpty();
    }

    public boolean hasAnyJobResults() {
        return !driver.findElements(jobLinks).isEmpty();
    }

    public void openFirstActiveJob() {
        wait.until(d -> hasNoResults() || hasAnyJobResults());

        if (hasNoResults() && !hasAnyJobResults()) {
            throw new AssertionError("No active job found for the provided keyword.");
        }

        List<WebElement> links = driver.findElements(jobLinks);
        if (links.isEmpty()) {
            throw new AssertionError("Results loaded but no job links found.");
        }

        Exception lastError = null;

        for (WebElement link : links) {
            try {
                if (!link.isDisplayed()) continue;

                String href = link.getAttribute("href");
                if (href == null || href.isBlank()) continue;

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", link
                );

                try {
                    wait.until(ExpectedConditions.elementToBeClickable(link));
                } catch (Exception ignored) {}

                try {
                    link.click();
                } catch (ElementNotInteractableException e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
                }

                wait.until(d -> !d.getCurrentUrl().toLowerCase().contains("search"));
                return;

            } catch (Exception e) {
                lastError = e;
            }
        }

        throw new AssertionError("Could not click any visible/interactable job result link.", lastError);
    }
}