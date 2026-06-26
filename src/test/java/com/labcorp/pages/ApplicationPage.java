package com.labcorp.pages;

import com.labcorp.utils.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ApplicationPage extends BasePage {

    private final By titlePrimary = By.cssSelector("h1");
    private final By titleFallback = By.cssSelector("[data-automation-id*='title'], [class*='job-title']");

    private final By locationByData = By.cssSelector(
            "[data-automation-id*='locations'], [data-automation-id*='location'], [data-ph-id*='location']"
    );
    private final By locationByLabel = By.xpath(
            "//*[contains(translate(normalize-space(.),'LOCATION','location'),'location')]/following::*[1]"
    );
    private final By locationByClass = By.cssSelector("[class*='location'], [class*='job-location']");

    private final By idByData = By.cssSelector(
            "[data-automation-id*='jobId'], [data-automation-id*='req'], [data-ph-id*='jobId']"
    );
    private final By idByLabel = By.xpath(
            "//*[contains(translate(normalize-space(.),'JOB IDREQ ID','job idreq id'),'job id') " +
            "or contains(translate(normalize-space(.),'JOB IDREQ ID','job idreq id'),'req id')]/following::*[1]"
    );
    private final By idByClass = By.cssSelector("[class*='job-id'], [class*='req-id'], [class*='requisition']");

    private final By bodyText = By.cssSelector("main, body, [class*='content']");
    private final By returnToSearch = By.xpath(
            "//a[contains(.,'Return to Job Search') or contains(.,'Back to Job Search') or contains(.,'Search Jobs')]"
    );

    public String getTitle() {
        return firstNonBlank(titlePrimary, titleFallback);
    }

    public String getLocation() {
        return firstNonBlank(locationByData, locationByLabel, locationByClass);
    }

    public String getJobId() {
        return firstNonBlank(idByData, idByLabel, idByClass);
    }

    public String getBodyText() {
        return firstNonBlank(bodyText);
    }

    public boolean clickReturnToSearchIfPresent() {
        try {
            List<WebElement> links = driver.findElements(returnToSearch);
            if (links.isEmpty()) return false;

            WebElement el = links.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);

            try {
                wait.until(ExpectedConditions.elementToBeClickable(el));
                el.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void goToSearchPageDirectly() {
        driver.get("https://careers.labcorp.com/search-jobs");
    }

    private String firstNonBlank(By... locators) {
        for (By by : locators) {
            List<WebElement> elements = driver.findElements(by);
            for (WebElement e : elements) {
                String t = e.getText() == null ? "" : e.getText().trim();
                if (!t.isBlank()) return t;
            }
        }
        return "";
    }
}