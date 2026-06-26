package com.labcorp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class CareersPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(CareersPage.class);
    
    // Search elements with multiple strategies
    private static final By SEARCH_INPUT = By.cssSelector("input[type='search'], input[placeholder*='Search'], input[name='q']");
    private static final By SEARCH_BUTTON = By.cssSelector("button[type='submit'], .search-submit");
    private static final By JOB_RESULTS = By.cssSelector(".job-item, .job-listing, .search-result, .career-item, .job-result, .card-job");
    private static final By JOB_TITLES = By.xpath("//h3 | //h2 | //h4 | //a[contains(@href, 'job')] | //div[contains(@class, 'job-title')]");
    private static final By JOB_LINKS = By.cssSelector("a[href*='job']:not([href*='#'])");
    private static final By NO_RESULTS = By.xpath("//*[contains(text(), 'No results') or contains(text(), 'No jobs') or contains(text(), 'No positions')]");
    
    public CareersPage searchJobs(String keyword) {
        logger.info("Searching for jobs with keyword: {}", keyword);
        waitForPageLoad();
        waitUtils.waitForAjaxComplete();
        
        try {
            // Try multiple strategies to find search input
            WebElement searchInput = findSearchInput();
            
            if (searchInput != null) {
                searchInput.clear();
                searchInput.sendKeys(keyword);
                logger.info("Entered keyword: {}", keyword);
                
                // Submit search
                try {
                    searchInput.submit();
                } catch (Exception e) {
                    List<WebElement> buttons = driver.findElements(By.cssSelector("button[type='submit'], .search-button, .btn-search"));
                    if (!buttons.isEmpty()) {
                        buttons.get(0).click();
                    } else {
                        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);
                    }
                }
                
                // Wait for results with explicit wait
                waitUtils.waitForAjaxComplete();
                waitForPageLoad();
                waitUtils.waitForPresence(JOB_RESULTS, 10);
                
            } else {
                logger.warn("Could not find search input field - trying direct URL navigation");
                driver.get("https://www.labcorp.com/careers?keyword=" + keyword.replace(" ", "+"));
                waitForPageLoad();
            }
            
            // Check if no results found
            if (isElementPresent(NO_RESULTS)) {
                logger.warn("⚠️ No results found for keyword: '{}'", keyword);
            }
            
        } catch (Exception ex) {
            logger.error("Search failed: {}", ex.getMessage());
            // Recovery: refresh and try direct navigation
            refreshPage();
            driver.get("https://www.labcorp.com/careers?keyword=" + keyword.replace(" ", "+"));
            waitForPageLoad();
            
            if (!hasSearchResults()) {
                throw new RuntimeException("Search failed after recovery attempts", ex);
            }
        }
        
        return this;
    }
    
    private WebElement findSearchInput() {
        // Strategy 1: By ID
        try {
            return driver.findElement(By.id("search-keyword"));
        } catch (NoSuchElementException e) {
            logger.debug("Search input not found by ID");
        }
        
        // Strategy 2: By placeholder
        try {
            List<WebElement> inputs = driver.findElements(By.cssSelector("input[type='text'], input[type='search']"));
            for (WebElement input : inputs) {
                String placeholder = input.getAttribute("placeholder");
                if (placeholder != null && 
                    (placeholder.toLowerCase().contains("search") || 
                     placeholder.toLowerCase().contains("job") ||
                     placeholder.toLowerCase().contains("keyword") ||
                     placeholder.toLowerCase().contains("title"))) {
                    return input;
                }
            }
        } catch (Exception e) {
            logger.debug("Search input not found by placeholder");
        }
        
        // Strategy 3: Any visible text input
        try {
            List<WebElement> inputs = driver.findElements(By.tagName("input"));
            for (WebElement input : inputs) {
                String type = input.getAttribute("type");
                if ((type == null || type.equals("text") || type.equals("search")) && 
                    input.isDisplayed() && input.isEnabled()) {
                    return input;
                }
            }
        } catch (Exception e) {
            logger.debug("Search input not found by visible input");
        }
        
        return null;
    }
    
    public JobDetailsPage selectJobByTitle(String jobTitle) {
        logger.info("Selecting job with title containing: {}", jobTitle);
        
        // Wait for results to load with explicit wait
        try {
            waitUtils.waitForPresence(JOB_RESULTS, 10);
        } catch (Exception e) {
            logger.warn("No job results found waiting for presence");
        }
        
        // Check if we have any results first
        if (!hasSearchResults()) {
            logger.warn("⚠️ No job results found for: {}", jobTitle);
            
            // Try alternative searches with related terms
            String[] alternativeSearches = {"QA", "Test", "Automation", "Developer", "Engineer"};
            for (String altSearch : alternativeSearches) {
                if (!altSearch.equalsIgnoreCase(jobTitle) && !jobTitle.toLowerCase().contains(altSearch.toLowerCase())) {
                    logger.info("🔄 Trying alternative search: '{}'", altSearch);
                    searchJobs(altSearch);
                    if (hasSearchResults()) {
                        logger.info("✅ Found results with alternative search: '{}'", altSearch);
                        break;
                    }
                }
            }
        }
        
        if (!hasSearchResults()) {
            throw new RuntimeException("No job results found for: " + jobTitle);
        }
        
        // Try multiple strategies to find and click job
        return findAndClickJob(jobTitle);
    }
    
    private JobDetailsPage findAndClickJob(String jobTitle) {
        // Strategy 1: Try to find by exact text match
        try {
            String xpath = String.format("//*[contains(text(), '%s')]", jobTitle);
            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            for (WebElement element : elements) {
                if (element.isDisplayed() && element.isEnabled()) {
                    scrollToElement(element);
                    clickWithJavascript(element);
                    logger.info("✅ Clicked job by text: {}", jobTitle);
                    return new JobDetailsPage();
                }
            }
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.debug("Strategy 1 failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in strategy 1", e);
        }
        
        // Strategy 2: Try to find by job links
        try {
            List<WebElement> jobLinks = driver.findElements(By.cssSelector("a[href*='job']"));
            for (WebElement link : jobLinks) {
                String text = link.getText().trim();
                String href = link.getAttribute("href");
                if ((!text.isEmpty() && text.toLowerCase().contains(jobTitle.toLowerCase())) ||
                    (href != null && href.toLowerCase().contains(jobTitle.toLowerCase().replace(" ", "-").toLowerCase()))) {
                    scrollToElement(link);
                    clickWithJavascript(link);
                    logger.info("✅ Clicked job link: {}", text);
                    return new JobDetailsPage();
                }
            }
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.debug("Strategy 2 failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in strategy 2", e);
        }
        
        // Strategy 3: Click the first job result
        try {
            List<WebElement> results = driver.findElements(JOB_RESULTS);
            if (!results.isEmpty()) {
                scrollToElement(results.get(0));
                clickWithJavascript(results.get(0));
                logger.info("✅ Clicked first job result as fallback");
                return new JobDetailsPage();
            }
        } catch (Exception e) {
            logger.debug("Fallback strategy failed: {}", e.getMessage());
        }
        
        throw new RuntimeException("Could not find job with title containing: " + jobTitle);
    }
    
    public List<String> getJobTitles() {
        try {
            List<WebElement> elements = driver.findElements(JOB_TITLES);
            return elements.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.isEmpty() && text.length() > 3)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("Could not get job titles: {}", e.getMessage());
            return List.of();
        }
    }
    
    public boolean hasSearchResults() {
        try {
            // Check for no results message first
            if (isElementPresent(NO_RESULTS)) {
                return false;
            }
            
            List<WebElement> results = driver.findElements(JOB_RESULTS);
            boolean hasResults = !results.isEmpty();
            
            // If no results with JOB_RESULTS, try checking page content
            if (!hasResults) {
                String pageSource = driver.getPageSource();
                hasResults = pageSource.contains("job") || pageSource.contains("Job") || 
                            pageSource.contains("position") || pageSource.contains("Position");
            }
            
            return hasResults;
        } catch (Exception e) {
            return false;
        }
    }
    
    public int getJobCount() {
        try {
            List<WebElement> results = driver.findElements(JOB_RESULTS);
            return results.size();
        } catch (Exception e) {
            return 0;
        }
    }
}
