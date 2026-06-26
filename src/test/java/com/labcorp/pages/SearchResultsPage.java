package com.labcorp.pages;

import com.labcorp.utils.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
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

    private final Duration shortPollTimeout = Duration.ofSeconds(8);

    public boolean hasNoResults() {
        return !driver.findElements(noResults).isEmpty();
    }

    public boolean hasAnyJobResults() {
        WebDriverWait shortWait = new WebDriverWait(driver, shortPollTimeout);
        Boolean ready = shortWait.until(d -> {
            boolean foundResults = !d.findElements(jobLinks).isEmpty();
            boolean explicitNoResults = !d.findElements(noResults).isEmpty();
            return foundResults || explicitNoResults;
        });
        return Boolean.TRUE.equals(ready) && !driver.findElements(jobLinks).isEmpty();
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

        for (int i = 0; i < links.size(); i++) {
            try {
                List<WebElement> refreshed = driver.findElements(jobLinks);
                if (i >= refreshed.size()) break;

                WebElement link = refreshed.get(i);

                if (!link.isDisplayed()) continue;

                String href = link.getAttribute("href");
                if (href == null || href.isBlank()) continue;

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", link
                );

                try {
                    wait.until(ExpectedConditions.elementToBeClickable(link));
                    link.click();
                } catch (ElementNotInteractableException e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
                }

                wait.until(d -> {
                    String url = d.getCurrentUrl().toLowerCase();
                    return !url.contains("search-jobs") && !url.contains("/search");
                });
                return;

            } catch (StaleElementReferenceException e) {
                lastError = e;
            } catch (Exception e) {
                lastError = e;
            }
        }

        throw new AssertionError("Could not click any visible/interactable job result link.", lastError);
    }
}