package com.labcorp.pages;

import com.labcorp.utils.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

public class JobDetailsPage extends BasePage {

    // Title candidates
    private final By titlePrimary = By.cssSelector("h1");
    private final By titleFallback = By.xpath("//*[contains(@class,'job') and (self::h1 or self::h2)]");

    // Location candidates
    private final By locationByDataAttr = By.cssSelector(
            "[data-automation-id*='locations'], [data-automation-id*='location'], [data-ph-id*='location']"
    );
    private final By locationLabelPattern = By.xpath(
            "//*[contains(translate(normalize-space(.),'LOCATION','location'),'location')]/following::*[1]"
    );
    private final By locationClassPattern = By.cssSelector(
            "[class*='location'], [class*='job-location']"
    );

    // Job ID candidates
    private final By jobIdByDataAttr = By.cssSelector(
            "[data-automation-id*='jobId'], [data-automation-id*='req'], [data-ph-id*='jobId']"
    );
    private final By jobIdLabelPattern = By.xpath(
            "//*[contains(translate(normalize-space(.),'JOB IDREQ ID','job idreq id'),'job id') " +
            "or contains(translate(normalize-space(.),'JOB IDREQ ID','job idreq id'),'req id')]/following::*[1]"
    );
    private final By jobIdClassPattern = By.cssSelector(
            "[class*='job-id'], [class*='req-id'], [class*='requisition']"
    );

    private final By description = By.cssSelector("main, [data-automation-id='jobPostingDescription'], .job-description");
    private final By applyNow = By.xpath("//a[contains(.,'Apply') or contains(.,'apply')] | //button[contains(.,'Apply') or contains(.,'apply')]");

    public String getTitle() {
        return firstNonBlank(titlePrimary, titleFallback);
    }

    public String getLocation() {
        return firstNonBlank(locationByDataAttr, locationLabelPattern, locationClassPattern);
    }

    public String getJobId() {
        return firstNonBlank(jobIdByDataAttr, jobIdLabelPattern, jobIdClassPattern);
    }

    public String getDescriptionText() {
        return safeText(description);
    }

    public void assertCoreFieldsPresent() {
        String t = getTitle();
        String l = getLocation();
        String j = getJobId();

        if (t.isBlank()) throw new AssertionError("Job Title missing");
        if (l.isBlank()) throw new AssertionError("Job Location missing");
        if (j.isBlank()) throw new AssertionError("Job ID missing");
    }

    public void clickApplyNow() {
        scrollTo(applyNow);
        click(applyNow);
    }

    // ---------- helpers ----------

    private String firstNonBlank(By... locators) {
        for (By by : locators) {
            String val = safeText(by);
            if (!val.isBlank()) return val;
        }
        return "";
    }

    private String safeText(By by) {
        try {
            List<WebElement> els = driver.findElements(by);
            for (WebElement el : els) {
                String txt = el.getText() == null ? "" : el.getText().trim();
                if (!txt.isBlank()) return txt;
            }
            return "";
        } catch (TimeoutException e) {
            return "";
        }
    }
}