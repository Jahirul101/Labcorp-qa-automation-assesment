package com.labcorp.stepdefinitions;

import com.labcorp.pages.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LabCorpSteps {
    
    private static final Logger logger = LoggerFactory.getLogger(LabCorpSteps.class);
    
    private final HomePage homePage;
    private CareersPage careersPage;
    private JobDetailsPage jobDetailsPage;
    private JobApplicationPage jobApplicationPage;
    private String searchedJobTitle = "";
    
    public LabCorpSteps() {
        this.homePage = new HomePage();
    }
    
    @Given("I am on the LabCorp homepage")
    public void iAmOnTheLabCorpHomepage() {
        logger.info("Navigating to LabCorp homepage");
        homePage.navigateToHomePage();
        String pageTitle = homePage.getPageTitle();
        logger.info("Page title: {}", pageTitle);
        boolean isLoaded = homePage.isHomePageLoaded();
        logger.info("Homepage loaded: {}", isLoaded);
        Assertions.assertThat(isLoaded)
                .as("Homepage should be loaded. Title: " + pageTitle)
                .isTrue();
    }
    
    @When("I navigate to the Careers page")
    public void iNavigateToTheCareersPage() {
        logger.info("Navigating to Careers page");
        careersPage = homePage.navigateToCareers();
        Assertions.assertThat(careersPage).isNotNull();
    }
    
    @When("I search for job with keyword {string}")
    public void iSearchForJobWithKeyword(String keyword) {
        logger.info("Searching for job with keyword: {}", keyword);
        searchedJobTitle = keyword;
        careersPage.searchJobs(keyword);
        
        // Log the results
        boolean hasResults = careersPage.hasSearchResults();
        int jobCount = careersPage.getJobCount();
        logger.info("Search results for '{}' - Has results: {}, Count: {}", keyword, hasResults, jobCount);
        
        if (!hasResults) {
            logger.warn("⚠️ No jobs found for keyword: '{}'. The job may not be available on the site.", keyword);
            logger.info("💡 Tip: Try searching for a different job title like 'QA' or 'Test Automation'");
        }
    }
    
    @When("I select a job posting with title containing {string}")
    public void iSelectAJobPostingWithTitleContaining(String jobTitle) {
        logger.info("Selecting job posting with title containing: {}", jobTitle);
        jobDetailsPage = careersPage.selectJobByTitle(jobTitle);
        Assertions.assertThat(jobDetailsPage).isNotNull();
    }
    
    @When("I select the job posting with title {string}")
    public void iSelectTheJobPostingWithTitle(String jobTitle) {
        logger.info("Selecting job posting with title: {}", jobTitle);
        searchedJobTitle = jobTitle;
        jobDetailsPage = careersPage.selectJobByTitle(jobTitle);
        Assertions.assertThat(jobDetailsPage).isNotNull();
    }
    
    @Then("I should see job title, location, and ID displayed")
    public void iShouldSeeJobTitleLocationAndIdDisplayed() {
        logger.info("Verifying job title, location, and ID are displayed");
        String jobTitle = jobDetailsPage.getJobTitle();
        String jobLocation = jobDetailsPage.getJobLocation();
        String jobId = jobDetailsPage.getJobId();
        
        logger.info("Job Title: {}, Location: {}, ID: {}", jobTitle, jobLocation, jobId);
        
        Assertions.assertThat(jobTitle)
                .as("Job title should not be empty")
                .isNotEmpty();
        Assertions.assertThat(jobLocation)
                .as("Job location should not be empty")
                .isNotEmpty();
        Assertions.assertThat(jobId)
                .as("Job ID should not be empty")
                .isNotEmpty();
    }
    
    @Then("I should see the Apply Now button is present")
    public void iShouldSeeTheApplyNowButtonIsPresent() {
        logger.info("Verifying Apply Now button is present");
        boolean isPresent = jobDetailsPage.isApplyNowPresent();
        Assertions.assertThat(isPresent)
                .as("Apply Now button should be present on the page")
                .isTrue();
    }
    
    @When("I click the Apply Now button")
    public void iClickTheApplyNowButton() {
        jobApplicationPage = jobDetailsPage.clickApplyNow();
        Assertions.assertThat(jobApplicationPage).isNotNull();
    }
    
    @Then("I should be on the job application page")
    public void iShouldBeOnTheJobApplicationPage() {
        Assertions.assertThat(jobApplicationPage.isApplicationFormDisplayed())
                .as("Should be on application page with form displayed")
                .isTrue();
    }
    
    @When("I return to job search")
    public void iReturnToJobSearch() {
        careersPage = jobApplicationPage.returnToJobSearch();
        Assertions.assertThat(careersPage).isNotNull();
    }
    
    @Then("I should be on the careers search page")
    public void iShouldBeOnTheCareersSearchPage() {
        Assertions.assertThat(careersPage).isNotNull();
    }
    
    @Then("I should see the following job details:")
    public void iShouldSeeTheFollowingJobDetails(DataTable dataTable) {
        List<Map<String, String>> details = dataTable.asMaps(String.class, String.class);
        Map<String, String> expectedDetails = details.get(0);
        
        String expectedTitle = expectedDetails.get("Job Title");
        String expectedLocation = expectedDetails.get("Job Location");
        String expectedId = expectedDetails.get("Job ID");
        
        String actualTitle = jobDetailsPage.getJobTitle();
        String actualLocation = jobDetailsPage.getJobLocation();
        String actualId = jobDetailsPage.getJobId();
        
        logger.info("Actual - Title: {}, Location: {}, ID: {}", actualTitle, actualLocation, actualId);
        
        Assertions.assertThat(actualTitle)
                .as("Job title should match expected")
                .contains(expectedTitle);
        Assertions.assertThat(actualLocation)
                .as("Job location should match expected")
                .contains(expectedLocation);
        Assertions.assertThat(actualId)
                .as("Job ID should match expected")
                .contains(expectedId);
    }
    
    @Then("I should see the first description paragraph contains text {string}")
    public void iShouldSeeTheFirstDescriptionParagraphContainsText(String expectedText) {
        String firstParagraph = jobDetailsPage.getDescriptionParagraph(0);
        logger.info("First paragraph: {}", firstParagraph);
        Assertions.assertThat(firstParagraph)
                .as("First description paragraph should contain expected text")
                .contains(expectedText);
    }
    
    @Then("I should see the management support includes {string}")
    public void iShouldSeeTheManagementSupportIncludes(String expectedText) {
        List<String> supportPoints = jobDetailsPage.getManagementSupportPoints();
        logger.info("Management support points: {}", supportPoints);
        Assertions.assertThat(supportPoints)
                .as("Management support should include expected text")
                .anyMatch(point -> point.contains(expectedText));
    }
    
    @Then("I should see the requirements include {string}")
    public void iShouldSeeTheRequirementsInclude(String expectedText) {
        List<String> requirements = jobDetailsPage.getRequirements();
        logger.info("Requirements: {}", requirements);
        Assertions.assertThat(requirements)
                .as("Requirements should include expected text")
                .anyMatch(req -> req.contains(expectedText));
    }
    
    @Then("I should see suggested automation tools include {string}")
    public void iShouldSeeSuggestedAutomationToolsInclude(String expectedTool) {
        List<String> tools = jobDetailsPage.getAutomationSuggestions();
        logger.info("Automation tools: {}", tools);
        Assertions.assertThat(tools)
                .as("Automation tools should include expected tool")
                .anyMatch(tool -> tool.toLowerCase().contains(expectedTool.toLowerCase()));
    }
    
    @Then("I should see at least one job result")
    public void iShouldSeeAtLeastOneJobResult() {
        boolean hasResults = careersPage.hasSearchResults();
        int jobCount = careersPage.getJobCount();
        logger.info("Job results found: {}, count: {}", hasResults, jobCount);
        
        if (!hasResults) {
            logger.warn("⚠️ No jobs found for keyword: '{}'. The job may not be available on the site.", searchedJobTitle);
            logger.info("💡 Tip: Try searching for a different job title like 'QA' or 'Test Automation'");
            
            // Get all job titles for debugging
            List<String> titles = careersPage.getJobTitles();
            if (!titles.isEmpty()) {
                logger.info("Available job titles on page: {}", titles);
            }
        }
        
        Assertions.assertThat(hasResults)
                .as("Should have at least one job result. No jobs found for: " + searchedJobTitle)
                .isTrue();
        Assertions.assertThat(jobCount)
                .as("Job count should be greater than 0")
                .isGreaterThan(0);
    }
    
    @Then("I should see the search results contain {string} in the job titles")
    public void iShouldSeeTheSearchResultsContainInTheJobTitles(String keyword) {
        List<String> jobTitles = careersPage.getJobTitles();
        logger.info("Job titles found: {}", jobTitles);
        
        Assertions.assertThat(jobTitles)
                .as("Job titles should not be empty")
                .isNotEmpty();
        
        boolean containsKeyword = jobTitles.stream()
                .anyMatch(title -> title.toLowerCase().contains(keyword.toLowerCase()));
        
        Assertions.assertThat(containsKeyword)
                .as("At least one job title should contain keyword: " + keyword)
                .isTrue();
    }
}
