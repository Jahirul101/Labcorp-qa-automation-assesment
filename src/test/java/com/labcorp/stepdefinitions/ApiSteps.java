package com.labcorp.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ApiSteps {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiSteps.class);
    
    private Response response;
    private RequestSpecification request;
    private String endpoint;
    private String payload;
    private Map<String, Object> queryParams = new HashMap<>();
    
    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        this.endpoint = endpoint;
        request = RestAssured.given()
                .baseUri("https://echo.free.beeceptor.com")
                .contentType("application/json")
                .accept("application/json");
        
        if (!queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        
        response = request.get(endpoint);
        logger.info("GET Response Status: {}", response.getStatusCode());
        logger.info("GET Response Body: {}", response.getBody().asString());
    }
    
    @When("I send a POST request to {string}")
    public void iSendAPOSTRequestTo(String endpoint) {
        this.endpoint = endpoint;
        request = RestAssured.given()
                .baseUri("https://echo.free.beeceptor.com")
                .contentType("application/json")
                .accept("application/json");
    }
    
    @And("I include query parameter {string} with value {string}")
    public void iIncludeQueryParameterWithValue(String key, String value) {
        queryParams.put(key, value);
    }
    
    @And("I send the following order payload:")
    public void iSendTheFollowingOrderPayload(String payload) {
        this.payload = payload;
        if (!queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        response = request.body(payload).post(endpoint);
        logger.info("POST Response Status: {}", response.getStatusCode());
        logger.info("POST Response Body: {}", response.getBody().asString());
    }
    
    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        Assertions.assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }
    
    @Then("the response status code should be {int} or {int}")
    public void theResponseStatusCodeShouldBeOr(int statusCode1, int statusCode2) {
        Assertions.assertThat(response.getStatusCode()).isIn(statusCode1, statusCode2);
    }
    
    @And("the response should contain field {string}")
    public void theResponseShouldContainField(String field) {
        Assertions.assertThat(response.getBody().asString()).contains(field);
    }
    
    @And("the response should contain headers")
    public void theResponseShouldContainHeaders() {
        Assertions.assertThat(response.getHeaders()).isNotNull();
        Assertions.assertThat(response.getHeaders().size()).isGreaterThan(0);
    }
    
    @And("the response headers should include {string}")
    public void theResponseHeadersShouldInclude(String headerName) {
        Assertions.assertThat(response.getHeader(headerName)).isNotNull();
    }
    
    @And("the customer name should be {string}")
    public void theCustomerNameShouldBe(String expectedName) {
        Assertions.assertThat(payload).contains("\"name\": \"" + expectedName + "\"");
    }
    
    @And("the customer email should be {string}")
    public void theCustomerEmailShouldBe(String expectedEmail) {
        Assertions.assertThat(payload).contains("\"email\": \"" + expectedEmail + "\"");
    }
    
    @And("the payment method should be {string}")
    public void thePaymentMethodShouldBe(String expectedMethod) {
        Assertions.assertThat(payload).contains("\"method\": \"" + expectedMethod + "\"");
    }
    
    @And("the payment amount should be {double}")
    public void thePaymentAmountShouldBe(double expectedAmount) {
        Assertions.assertThat(payload).contains("\"amount\": " + expectedAmount);
    }
    
    @And("the product items should contain {string} with quantity {int}")
    public void theProductItemsShouldContainWithQuantity(String productName, int expectedQuantity) {
        Assertions.assertThat(payload).contains("\"name\": \"" + productName + "\"");
        Assertions.assertThat(payload).contains("\"quantity\": " + expectedQuantity);
    }
    
    @And("the response should include the {string} with value {string}")
    public void theResponseShouldIncludeTheWithValue(String field, String expectedValue) {
        Assertions.assertThat(payload).contains("\"" + field + "\": \"" + expectedValue + "\"");
    }
}
