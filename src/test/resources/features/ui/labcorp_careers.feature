Feature: LabCorp Careers Job Search and Verification

  As a job seeker
  I want to search and view job details on LabCorp careers page
  So that I can verify job information before applying

  Background:
    Given I am on the LabCorp homepage
    When I navigate to the Careers page

  @UI @Smoke
  Scenario: Search and verify a specific job posting
    When I search for job with keyword "QA Test Automation Developer"
    Then I should see at least one job result
    And I select a job posting with title containing "QA Test Automation Developer"
    Then I should see job title, location, and ID displayed
    And I should see the Apply Now button is present
    When I click the Apply Now button
    Then I should be on the job application page
    When I return to job search
    Then I should be on the careers search page

  @UI @Regression
  Scenario: Search for jobs by keyword and verify results
    When I search for job with keyword "Test Automation"
    Then I should see at least one job result
    And I should see the search results contain "Test Automation" in the job titles
