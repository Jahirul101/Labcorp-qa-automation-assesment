Feature: API GET Request Validation

  @API @Smoke
  Scenario: Validate GET request with query parameter
    When I send a GET request to "/sample-request"
    And I include query parameter "author" with value "beeceptor"
    Then the response status code should be 200
    And the response should contain field "path"
    And the response should contain field "ip"
    And the response should contain headers
    And the response headers should include "Content-Type"

  @API @Regression
  Scenario: Validate GET request without query parameters
    When I send a GET request to "/sample-request"
    Then the response status code should be 200
    And the response should contain field "path"
