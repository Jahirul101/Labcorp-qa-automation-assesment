package com.labcorp.pages;

import org.openqa.selenium.By;
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
        
        try {
            // Wait for page to be ready
            Thread.sleep(3000);
            
            // Try multiple strategies to find search input
            WebElement searchInput = null;
            
            // Strategy 1: Find by ID
            try {
                searchInput = driver.findElement(By.id("search-keyword"));
                logger.debug("Found search input by ID: search-keyword");
            } catch (Exception e) {
                // Continue to next strategy
            }
            
            // Strategy 2: Find by placeholder
            if (searchInput == null) {
                List<WebElement> inputs = driver.findElements(By.cssSelector("input[type='text'], input[type='search']"));
                for (WebElement input : inputs) {
                    String placeholder = input.getAttribute("placeholder");
                    if (placeholder != null && 
                        (placeholder.toLowerCase().contains("search") || 
                         placeholder.toLowerCase().contains("job") ||
                         placeholder.toLowerCase().contains("keyword") ||
                         placeholder.toLowerCase().contains("title"))) {
                        searchInput = input;
                        logger.debug("Found search input by placeholder: {}", placeholder);
                        break;
                    }
                }
            }
            
            // Strategy 3: Find any visible input
            if (searchInput == null) {
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                for (WebElement input : inputs) {
                    String type = input.getAttribute("type");
                    if ((type == null || type.equals("text") || type.equals("search")) && 
                        input.isDisplayed() && input.isEnabled()) {
                        searchInput = input;
                        logger.debug("Found search input by visible text input");
                        break;
                    }
                }
            }
            
            if (searchInput != null) {
                // Clear and enter keyword
                searchInput.clear();
                searchInput.sendKeys(keyword);
                logger.info("Entered keyword: {}", keyword);
                
                // Try to submit
                try {
                    searchInput.submit();
                    logger.debug("Submitted search form");
                } catch (Exception e) {
                    // Find and click search button
                    List<WebElement> buttons = driver.findElements(By.cssSelector("button[type='submit'], .search-button, .btn-search"));
                    if (!buttons.isEmpty()) {
                        buttons.get(0).click();
                        logger.debug("Clicked search button");
                    } else {
                        // Press Enter key
                        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);
                        logger.debug("Pressed Enter key");
                    }
                }
                
                // Wait for results
                Thread.sleep(5000);
                waitForPageLoad();
                
            } else {
                logger.warn("Could not find search input field - trying direct URL navigation");
                // Try direct navigation with query param
                driver.get("https://www.labcorp.com/careers?keyword=" + keyword.replace(" ", "+"));
                Thread.sleep(5000);
                waitForPageLoad();
            }
            
            // Check if no results found
            if (isElementPresent(NO_RESULTS)) {
                logger.warn("⚠️ No results found for keyword: '{}'", keyword);
            }
            
        } catch (Exception ex) {
            logger.error("Search failed: {}", ex.getMessage());
            throw new RuntimeException("Search failed: " + ex.getMessage(), ex);
        }
        
        return this;
    }
    
    public JobDetailsPage selectJobByTitle(String jobTitle) {
        logger.info("Selecting job with title containing: {}", jobTitle);
        
        // Wait for results to load
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
            
            // Try to search by parts of the job title
            if (!hasSearchResults()) {
                String[] parts = jobTitle.split(" ");
                for (String part : parts) {
                    if (part.length() > 3) {
                        logger.info("🔄 Trying search with part: '{}'", part);
                        searchJobs(part);
                        if (hasSearchResults()) {
                            logger.info("✅ Found results with part: '{}'", part);
                            break;
                        }
                    }
                }
            }
        }
        
        if (!hasSearchResults()) {
            throw new RuntimeException("No job results found for: " + jobTitle + ". Please check the search term.");
        }
        
        // Strategy 1: Try to find by exact text match
        try {
            String xpath = String.format("//*[contains(text(), '%s')]", jobTitle);
            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            for (WebElement element : elements) {
                if (element.isDisplayed() && element.isEnabled()) {
                    jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
                    jsExecutor.executeScript("arguments[0].click();", element);
                    logger.info("✅ Clicked job by text: {}", jobTitle);
                    return new JobDetailsPage();
                }
            }
        } catch (Exception ex) {
            logger.debug("Strategy 1 failed: {}", ex.getMessage());
        }
        
        // Strategy 2: Try to find by job links
        try {
            List<WebElement> jobLinks = driver.findElements(By.cssSelector("a[href*='job']"));
            for (WebElement link : jobLinks) {
                String text = link.getText().trim();
                String href = link.getAttribute("href");
                if ((!text.isEmpty() && text.toLowerCase().contains(jobTitle.toLowerCase())) ||
                    (href != null && href.toLowerCase().contains(jobTitle.toLowerCase().replace(" ", "-").toLowerCase()))) {
                    jsExecutor.executeScript("arguments[0].scrollIntoView(true);", link);
                    jsExecutor.executeScript("arguments[0].click();", link);
                    logger.info("✅ Clicked job link: {}", text);
                    return new JobDetailsPage();
                }
            }
        } catch (Exception ex) {
            logger.debug("Strategy 2 failed: {}", ex.getMessage());
        }
        
        // Strategy 3: Try to find by job title heading
        try {
            List<WebElement> headings = driver.findElements(By.cssSelector("h3, h2, h4"));
            for (WebElement heading : headings) {
                String text = heading.getText().trim();
                if (text.toLowerCase().contains(jobTitle.toLowerCase())) {
                    try {
                        WebElement parentLink = heading.findElement(By.xpath("./ancestor::a"));
                        jsExecutor.executeScript("arguments[0].click();", parentLink);
                    } catch (Exception ex) {
                        jsExecutor.executeScript("arguments[0].click();", heading);
                    }
                    logger.info("✅ Clicked job heading: {}", text);
                    return new JobDetailsPage();
                }
            }
        } catch (Exception ex) {
            logger.debug("Strategy 3 failed: {}", ex.getMessage());
        }
        
        // Strategy 4: Click the first job result
        try {
            List<WebElement> results = driver.findElements(JOB_RESULTS);
            if (!results.isEmpty()) {
                jsExecutor.executeScript("arguments[0].scrollIntoView(true);", results.get(0));
                jsExecutor.executeScript("arguments[0].click();", results.get(0));
                logger.info("✅ Clicked first job result as fallback");
                return new JobDetailsPage();
            }
        } catch (Exception ex) {
            logger.debug("Fallback strategy failed: {}", ex.getMessage());
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
        } catch (Exception ex) {
            logger.warn("Could not get job titles: {}", ex.getMessage());
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
        } catch (Exception ex) {
            return false;
        }
    }
    
    public int getJobCount() {
        try {
            List<WebElement> results = driver.findElements(JOB_RESULTS);
            return results.size();
        } catch (Exception ex) {
            return 0;
        }
    }
}
