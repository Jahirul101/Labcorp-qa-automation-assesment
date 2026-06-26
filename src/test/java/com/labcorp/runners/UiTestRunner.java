package com.labcorp.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@CucumberOptions(
        features = {"src/test/resources/features/ui"},
        glue = {"com.labcorp.stepdefinitions", "com.labcorp.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/ui-report.html",
                "json:target/cucumber-reports/ui-report.json"
        },
        monochrome = true,
        tags = "@UI"
)
public class UiTestRunner extends AbstractTestNGCucumberTests {
    
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
