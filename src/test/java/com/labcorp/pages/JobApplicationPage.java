package com.labcorp.pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobApplicationPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(JobApplicationPage.class);
    
    private static final By JOB_TITLE = By.cssSelector(".job-title, .position-title");
    private static final By JOB_LOCATION = By.cssSelector(".location, .job-location");
    private static final By JOB_ID = By.cssSelector(".job-id, .requisition-id");
    private static final By RETURN_TO_SEARCH = By.cssSelector(".return-to-search, .back-to-search");
    private static final By APPLY_FORM = By.id("apply-form");
    
    public JobApplicationPage() {
        super();
        waitForPageLoad();
    }
    
    public String getJobTitle() {
        try {
            if (isElementPresent(JOB_TITLE)) {
                return getText(JOB_TITLE);
            }
        } catch (Exception e) {
            logger.warn("Could not get job title from application page");
        }
        return "";
    }
    
    public String getJobLocation() {
        try {
            if (isElementPresent(JOB_LOCATION)) {
                return getText(JOB_LOCATION);
            }
        } catch (Exception e) {
            logger.warn("Could not get job location from application page");
        }
        return "";
    }
    
    public String getJobId() {
        try {
            if (isElementPresent(JOB_ID)) {
                return getText(JOB_ID);
            }
        } catch (Exception e) {
            logger.warn("Could not get job ID from application page");
        }
        return "";
    }
    
    public boolean verifyApplicationDetails(String expectedTitle, String expectedLocation, String expectedId) {
        String title = getJobTitle();
        String location = getJobLocation();
        String id = getJobId();
        
        boolean titleMatch = title.equalsIgnoreCase(expectedTitle) || title.contains(expectedTitle);
        boolean locationMatch = location.contains(expectedLocation) || location.equalsIgnoreCase(expectedLocation);
        boolean idMatch = id.contains(expectedId);
        
        logger.info("Application verification - Title: {}, Location: {}, ID: {}", titleMatch, locationMatch, idMatch);
        return titleMatch && locationMatch && idMatch;
    }
    
    public CareersPage returnToJobSearch() {
        logger.info("Returning to job search");
        try {
            if (isElementPresent(RETURN_TO_SEARCH)) {
                click(RETURN_TO_SEARCH);
            } else {
                // Fixed: using driver from BasePage (now accessible because it's protected)
                driver.navigate().back();
            }
        } catch (Exception e) {
            // Fixed: using driver from BasePage (now accessible because it's protected)
            driver.navigate().back();
        }
        waitForPageLoad();
        return new CareersPage();
    }
    
    public boolean isApplicationFormDisplayed() {
        // Fixed: using containsText() method from BasePage (protected, accessible here)
        return isElementDisplayed(APPLY_FORM) || containsText("Apply for this job");
    }
}
