package com.labcorp.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.labcorp.stepdefinitions", "com.labcorp.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/all-report.html",
                "json:target/cucumber-reports/all-report.json"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
    
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
