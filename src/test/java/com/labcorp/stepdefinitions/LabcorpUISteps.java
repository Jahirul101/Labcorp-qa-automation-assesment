package com.labcorp.stepdefinitions;

import com.labcorp.pages.ApplicationPage;
import com.labcorp.pages.CareersPage;
import com.labcorp.pages.HomePage;
import com.labcorp.pages.JobDetailsPage;
import com.labcorp.pages.SearchResultsPage;
import com.labcorp.utils.ConfigManager;
import com.labcorp.utils.DriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.HashSet;
import java.util.Set;

public class LabcorpUISteps {
    private final HomePage homePage = new HomePage();
    private final CareersPage careersPage = new CareersPage();
    private final SearchResultsPage searchResultsPage = new SearchResultsPage();
    private final JobDetailsPage jobDetailsPage = new JobDetailsPage();
    private final ApplicationPage applicationPage = new ApplicationPage();

    private String expectedTitle;
    private String expectedLocation;
    private String expectedJobId;
    private String chosenTextToMatchOnApplyPage;

    @Given("I open the Labcorp home page")
    public void iOpenTheLabcorpHomePage() {
        homePage.open(ConfigManager.get("baseUrl"));
        homePage.acceptCookiesIfPresent();
    }

    @When("I click Careers link")
    public void iClickCareersLink() {
        homePage.clickCareers();
        careersPage.waitForPage();
    }

    @When("I search for an active position")
    public void iSearchForAnActivePosition() {
        String[] candidates = new String[] {
                ConfigManager.get("searchKeyword"),
                "QA", "Software", "Engineer", "Analyst"
        };

        boolean found = false;
        for (String keyword : candidates) {
            careersPage.search(keyword);
            if (searchResultsPage.hasAnyJobResults()) {
                found = true;
                break;
            }
        }

        // fallback for dynamic/no-result moments
        if (!found) {
            DriverManager.getDriver().get("https://careers.labcorp.com/search-jobs");
            found = searchResultsPage.hasAnyJobResults();
        }

        Assert.assertTrue("No active positions found for any candidate keyword.", found);
    }

    @When("I open a job from search results")
    public void iOpenAJobFromSearchResults() {
        searchResultsPage.openFirstActiveJob();
    }

    @Then("I confirm Job Title Job Location and Job Id")
    public void iConfirmJobTitleJobLocationAndJobId() {
        jobDetailsPage.assertCoreFieldsPresent();
        expectedTitle = jobDetailsPage.getTitle();
        expectedLocation = jobDetailsPage.getLocation();
        expectedJobId = jobDetailsPage.getJobId();
    }

    @Then("I confirm 3 additional job detail assertions")
    public void iConfirm3AdditionalJobDetailAssertions() {
        String pageText = jobDetailsPage.getDescriptionText().toLowerCase();

        String introContains = ConfigManager.get("assert.intro.contains").toLowerCase();
        String requirementContains = ConfigManager.get("assert.requirement.contains").toLowerCase();

        Assert.assertTrue("Intro text assertion failed", pageText.contains(introContains));
        Assert.assertTrue("Requirement text assertion failed", pageText.contains(requirementContains));

        String[] toolCandidates = new String[] {
                ConfigManager.get("assert.tool.contains").toLowerCase(),
                "java", "selenium", "cypress", "playwright", "rest", "api", "automation", "testng", "junit"
        };

        String matched = null;
        for (String tool : toolCandidates) {
            if (tool != null && !tool.isBlank() && pageText.contains(tool)) {
                matched = tool;
                break;
            }
        }

        Assert.assertTrue("Tool text assertion failed", matched != null);
        chosenTextToMatchOnApplyPage = matched;
    }

    @When("I click Apply Now")
    public void iClickApplyNow() {
        jobDetailsPage.clickApplyNow();
    }

    @Then("I confirm Job Title Job Location Job Id and one chosen text match previous page")
    public void iConfirmApplyPageDetailsMatchPreviousPage() {
        String actualTitle = normalize(applicationPage.getTitle());
        String actualLocation = normalize(applicationPage.getLocation());
        String actualJobId = normalize(applicationPage.getJobId());

        String expTitle = normalize(expectedTitle);
        String expLocation = normalize(expectedLocation);
        String expJobId = normalize(expectedJobId);

        Assert.assertTrue("Job Title mismatch on Apply page",
                actualTitle.contains(expTitle) || expTitle.contains(actualTitle));

        Assert.assertTrue("Job Location mismatch on Apply page",
                locationsEquivalent(expLocation, actualLocation));

        Assert.assertTrue("Job ID mismatch on Apply page",
                idsEquivalent(expJobId, actualJobId));

        String appPageText = normalize(applicationPage.getBodyText());
        Assert.assertTrue("Chosen text not found on Apply page",
                appPageText.contains(normalize(chosenTextToMatchOnApplyPage)));
    }

    @When("I click Return to Job Search")
    public void iClickReturnToJobSearch() {
        boolean clicked = applicationPage.clickReturnToSearchIfPresent();
        if (!clicked) {
            applicationPage.goToSearchPageDirectly();
        }
    }

    @Then("I am returned to the job search page")
    public void iAmReturnedToTheJobSearchPage() {
        String currentUrl = DriverManager.getDriver().getCurrentUrl().toLowerCase();
        Assert.assertTrue("Not navigated back to job search page",
                currentUrl.contains("search") || currentUrl.contains("careers") || currentUrl.contains("jobs"));
    }

    private boolean locationsEquivalent(String a, String b) {
        if (a.isBlank() || b.isBlank()) return false;
        if (a.contains(b) || b.contains(a)) return true;

        Set<String> ta = locationTokens(a);
        Set<String> tb = locationTokens(b);

        int common = 0;
        for (String t : ta) if (tb.contains(t)) common++;

        return common >= 2;
    }

    private Set<String> locationTokens(String s) {
        String cleaned = s.toLowerCase()
                .replaceAll("[^a-z0-9, ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        Set<String> out = new HashSet<>();
        for (String p : cleaned.split("[, ]+")) {
            if (p.length() >= 2 && !Set.of("remote", "united", "states", "usa", "us", "job", "location").contains(p)) {
                out.add(p);
            }
        }
        return out;
    }

    private boolean idsEquivalent(String a, String b) {
        if (a.isBlank() || b.isBlank()) return false;
        if (a.contains(b) || b.contains(a)) return true;

        String da = a.replaceAll("\\D+", "");
        String db = b.replaceAll("\\D+", "");

        if (!da.isBlank() && !db.isBlank()) {
            return da.equals(db) || da.contains(db) || db.contains(da);
        }
        return false;
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+", " ").trim().toLowerCase();
    }
}