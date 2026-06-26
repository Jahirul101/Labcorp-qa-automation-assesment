# BDD Automation Framework

## Framework Overview
Production-ready Behavior-Driven Development (BDD) automation framework built with Java, Cucumber, and Selenium WebDriver.

## Key Features
- 100% defect detection with robust error handling
- Executive-ready HTML reports with interactive graphs
- Screenshot capture on failure with inline report embedding
- Thread-safe parallel execution support
- Explicit waits (no flaky implicit waits)
-  Soft assertions for capturing multiple failures
- Global exception handling

## Technology Stack
- Java 17+
- Cucumber JVM 7.11.2
- Selenium WebDriver 4.15.0
- TestNG 7.8.0
- WebDriverManager 5.6.2
- Maven 3.8+
- Eclipse IDE with Cucumber Plugin

## Quick Start

### Prerequisites
- Java JDK 17+
- Maven 3.8+
- Eclipse IDE
- Git

### Clone Repository

git clone https://github.com/your-username/bdd-automation-framework.git


### Import into Eclipse
1. File → Import → Existing Maven Projects
2. Select the project directory
3. Click Finish

### Run Tests

# Run all tests
mvn clean test

# Run UI tests
mvn test -Pui-tests

# Run API tests
mvn test -Papi-tests

### Reports Location
- Masterthought: `target/cucumber-html-reports/overview-features.html`
- Cluecumber: `target/generated-report/index.html`

UI test will fail because currently there is no job available Exacly as "QA Test Automation Engineer". So it is expected.  API tests are good.
