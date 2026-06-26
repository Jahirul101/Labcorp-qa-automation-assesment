# BDD Automation Framework

## Framework Overview
Assesment for Labcorp. Test automation framework built with Java, Cucumber, and Selenium WebDriver.


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
bash
git clone https://github.com/your-username/bdd-automation-framework.git


### Import into Eclipse
1. File → Import → Existing Maven Projects
2. Select the project directory
3. Click Finish

### Run Tests
bash
# Run all tests
mvn clean test

Run UI test:
mvn test -Pui-tests

Run API tests:
mvn test -Papi-tests
# Run tests in parallel (faster execution)
mvn test -Pparallel

# Run only smoke tests (both UI and API)
mvn test -Psmoke

# Run a specific feature file
mvn test -Dcucumber.features="src/test/resources/features/api/get_request.feature"

# Run scenarios with specific tags
mvn test -Dcucumber.filter.tags="@Smoke"
mvn test -Dcucumber.filter.tags="@API"
mvn test -Dcucumber.filter.tags="@UI"

# Generate reports only
mvn cluecumber-report:reporting

### Reports Location
- Masterthought: `target/cucumber-html-reports/overview-features.html`
- Cluecumber: `target/generated-report/index.html`
