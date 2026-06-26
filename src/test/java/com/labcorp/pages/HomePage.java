package com.labcorp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HomePage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(HomePage.class);
    
    // Updated resilient XPath locators for Careers link
    // Primary: Direct text match on anchor
    private static final By CAREERS_LINK = By.xpath("//a[text()='Careers']");
    
    // Fallback: Scoped within component text blocks with contains
    private static final By CAREERS_LINK_FALLBACK = By.xpath("//div[contains(@class, 'cmp-text')]//a[contains(text(), 'Careers')]");
    
    // Cookie accept button
    private static final By COOKIE_ACCEPT_BUTTON = By.xpath("//*[@id='onetrust-accept-btn-handler']");
    
    private static final String BASE_URL = "https://www.labcorp.com/";
    
    public HomePage navigateToHomePage() {
        logger.info("Navigating to LabCorp home page: {}", BASE_URL);
        driver.get(BASE_URL);
        waitForPageLoad();
        acceptCookiesIfPresent();
        return this;
    }
    
    private void acceptCookiesIfPresent() {
        try {
            if (isElementPresent(COOKIE_ACCEPT_BUTTON)) {
                click(COOKIE_ACCEPT_BUTTON);
                waitUtils.waitForInvisibility(COOKIE_ACCEPT_BUTTON);
                logger.info("Cookies accepted using OneTrust button");
            }
        } catch (Exception e) {
            logger.debug("Cookie banner not present or already accepted");
        }
    }
    
    public CareersPage navigateToCareers() {
        logger.info("Navigating to Careers page");
        waitForPageLoad();
        
        boolean clicked = false;
        
        // Strategy 1: Direct text match on anchor
        try {
            if (isElementPresent(CAREERS_LINK)) {
                click(CAREERS_LINK);
                clicked = true;
                logger.info("Clicked Careers link using primary XPath: //a[text()='Careers']");
            }
        } catch (Exception e) {
            logger.debug("Strategy 1 (primary XPath) failed: {}", e.getMessage());
        }
        
        // Strategy 2: Scoped within component text blocks
        if (!clicked) {
            try {
                if (isElementPresent(CAREERS_LINK_FALLBACK)) {
                    click(CAREERS_LINK_FALLBACK);
                    clicked = true;
                    logger.info("Clicked Careers link using fallback XPath: //div[contains(@class, 'cmp-text')]//a[contains(text(), 'Careers')]");
                }
            } catch (Exception e) {
                logger.debug("Strategy 2 (fallback XPath) failed: {}", e.getMessage());
            }
        }
        
        // Strategy 3: Find any link with careers text or href
        if (!clicked) {
            try {
                List<WebElement> allLinks = driver.findElements(By.tagName("a"));
                for (WebElement link : allLinks) {
                    String text = link.getText().trim();
                    String href = link.getAttribute("href");
                    if ((text.equalsIgnoreCase("Careers") || text.contains("Careers")) || 
                        (href != null && href.contains("/careers"))) {
                        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", link);
                        jsExecutor.executeScript("arguments[0].click();", link);
                        clicked = true;
                        logger.info("Clicked Careers link found by text search: '{}'", text);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.debug("Strategy 3 (link search) failed: {}", e.getMessage());
            }
        }
        
        if (!clicked) {
            logger.info("Direct navigation to careers page as fallback");
            driver.get("https://www.labcorp.com/careers");
        }
        
        waitForPageLoad();
        switchToNewWindow();
        return new CareersPage();
    }
    
    public boolean isHomePageLoaded() {
        try {
            waitForPageLoad();
            boolean hasCareersLink = isElementPresent(CAREERS_LINK) || 
                                    isElementPresent(CAREERS_LINK_FALLBACK);
            String title = driver.getTitle();
            boolean hasTitle = title != null && 
                              (title.contains("LabCorp") || 
                               title.contains("Labcorp"));
            
            logger.info("Homepage loaded check - Careers link: {}, Title: {}", hasCareersLink, hasTitle);
            return hasCareersLink || hasTitle;
        } catch (Exception e) {
            logger.error("Error checking if homepage is loaded: {}", e.getMessage());
            return false;
        }
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }
}
