package com.labcorp.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiHooks {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiHooks.class);
    
    @Before("@API")
    public void setUpApiTests() {
        logger.info("Setting up API test execution");
    }
    
    @After("@API")
    public void tearDownApiTests(Scenario scenario) {
        logger.info("API Scenario '{}' completed with status: {}", 
                scenario.getName(), scenario.getStatus());
    }
}
