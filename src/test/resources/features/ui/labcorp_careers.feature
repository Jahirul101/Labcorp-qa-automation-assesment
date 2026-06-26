@ui @labcorp
Feature: Labcorp Careers job posting validation

  Scenario: Browse to a Labcorp job and validate details through apply flow
    Given I open the Labcorp home page
    When I click Careers link
    And I search for an active position
    And I open a job from search results
    Then I confirm Job Title Job Location and Job Id
    And I confirm 3 additional job detail assertions
    When I click Apply Now
    Then I confirm Job Title Job Location Job Id and one chosen text match previous page
    When I click Return to Job Search
    Then I am returned to the job search page