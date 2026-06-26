package com.labcorp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class JobDetailsPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(JobDetailsPage.class);
    
    // Multiple By strategies
    private static final By JOB_TITLE = By.id("job-title");
    private static final By JOB_ID = By.id("job-id");
    private static final By APPLY_NOW_BUTTON = By.id("apply-now");
    private static final By JOB_LOCATION = By.cssSelector(".location, .job-location");
    private static final By JOB_TITLE_XPATH = By.xpath("//h1[contains(@class, 'job-title')]");
    private static final By DESCRIPTION_PARAGRAPHS = By.xpath("//div[contains(@class, 'description')]//p");
    private static final By MANAGEMENT_SUPPORT = By.xpath("//*[contains(text(), 'Management Support')]//following::li");
    private static final By REQUIREMENTS_LIST = By.xpath("//div[contains(@class, 'requirements')]//li");
    private static final By AUTO_SUGGESTION = By.cssSelector(".automation-tools li");
    
    public JobDetailsPage() {
        super();
        waitForPageLoad();
        waitUtils.waitForAjaxComplete();
    }
    
    public String getJobTitle() {
        try {
            if (isElementPresent(JOB_TITLE)) {
                return getText(JOB_TITLE);
            }
            if (isElementPresent(JOB_TITLE_XPATH)) {
                return getText(JOB_TITLE_XPATH);
            }
            List<WebElement> titles = findElements(By.cssSelector("h1, h2, .job-title"));
            for (WebElement title : titles) {
                String text = title.getText().trim();
                if (!text.isEmpty()) {
                    return text;
                }
            }
        } catch (Exception e) {
            logger.debug("Error getting job title: {}", e.getMessage());
        }
        return "";
    }
    
    public String getJobLocation() {
        try {
            if (isElementPresent(JOB_LOCATION)) {
                return getText(JOB_LOCATION);
            }
            By locationXpath = By.xpath("//*[contains(@class, 'location')]");
            if (isElementPresent(locationXpath)) {
                return getText(locationXpath);
            }
        } catch (Exception e) {
            logger.debug("Error getting job location: {}", e.getMessage());
        }
        return "";
    }
    
    public String getJobId() {
        try {
            if (isElementPresent(JOB_ID)) {
                return getText(JOB_ID);
            }
            By idXpath = By.xpath("//*[contains(text(), 'Job ID')]/following-sibling::*");
            if (isElementPresent(idXpath)) {
                return getText(idXpath);
            }
        } catch (Exception e) {
            logger.debug("Error getting job ID: {}", e.getMessage());
        }
        return "";
    }
    
    public String getDescriptionParagraph(int index) {
        try {
            List<WebElement> paragraphs = findElements(DESCRIPTION_PARAGRAPHS);
            if (paragraphs.size() > index) {
                return paragraphs.get(index).getText().trim();
            }
        } catch (Exception e) {
            logger.warn("Could not get description paragraph at index: {}", index);
        }
        return "";
    }
    
    public List<String> getManagementSupportPoints() {
        try {
            List<WebElement> points = findElements(MANAGEMENT_SUPPORT);
            return points.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public List<String> getRequirements() {
        try {
            List<WebElement> requirements = findElements(REQUIREMENTS_LIST);
            return requirements.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public List<String> getAutomationSuggestions() {
        try {
            List<WebElement> tools = findElements(AUTO_SUGGESTION);
            if (!tools.isEmpty()) {
                return tools.stream()
                        .map(WebElement::getText)
                        .filter(text -> !text.isEmpty())
                        .collect(Collectors.toList());
            }
            By toolsXpath = By.xpath("//*[contains(text(), 'Selenium')]//ancestor::li");
            tools = findElements(toolsXpath);
            return tools.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public boolean isApplyNowPresent() {
        try {
            boolean byId = isElementPresent(APPLY_NOW_BUTTON);
            boolean byXpath = isElementPresent(By.xpath("//button[contains(text(), 'Apply Now')]"));
            boolean byXpath2 = isElementPresent(By.xpath("//a[contains(text(), 'Apply Now')]"));
            boolean byContains = containsText("Apply Now");
            
            logger.info("Apply Now presence - By ID: {}, By XPath: {}, By XPath2: {}, Contains: {}", 
                       byId, byXpath, byXpath2, byContains);
            
            return byId || byXpath || byXpath2 || byContains;
        } catch (Exception e) {
            logger.debug("Error checking Apply Now presence: {}", e.getMessage());
            return false;
        }
    }
    
    public JobApplicationPage clickApplyNow() {
        logger.info("Clicking Apply Now button");
        WebElement applyButton = null;
        
        try {
            applyButton = findElement(APPLY_NOW_BUTTON);
        } catch (NoSuchElementException e) {
            logger.debug("Apply Now button not found by ID");
        } catch (Exception e) {
            logger.warn("Unexpected error finding Apply Now button by ID: {}", e.getMessage());
        }
        
        if (applyButton == null) {
            try {
                applyButton = findElement(By.xpath("//button[contains(text(), 'Apply Now')]"));
            } catch (NoSuchElementException e) {
                logger.debug("Apply Now button not found by XPath");
            } catch (Exception e) {
                logger.warn("Unexpected error finding Apply Now button by XPath: {}", e.getMessage());
            }
        }
        
        if (applyButton == null) {
            try {
                applyButton = findElement(By.xpath("//a[contains(text(), 'Apply Now')]"));
            } catch (NoSuchElementException e) {
                logger.debug("Apply Now link not found by XPath");
            } catch (Exception e) {
                logger.warn("Unexpected error finding Apply Now link by XPath: {}", e.getMessage());
            }
        }
        
        if (applyButton == null) {
            try {
                applyButton = findElement(By.xpath("//button[contains(text(), 'Apply')]"));
            } catch (NoSuchElementException e) {
                logger.debug("Apply button not found by XPath");
            } catch (Exception e) {
                logger.warn("Unexpected error finding Apply button by XPath: {}", e.getMessage());
            }
        }
        
        if (applyButton == null) {
            throw new NoSuchElementException("Apply Now button not found on page");
        }
        
        scrollToElement(applyButton);
        clickWithJavascript(applyButton);
        waitForPageLoad();
        return new JobApplicationPage();
    }
}
