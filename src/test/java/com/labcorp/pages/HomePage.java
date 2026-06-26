package com.labcorp.pages;

import com.labcorp.utils.BasePage;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private final By cookieAcceptBtn = By.cssSelector("#onetrust-accept-btn-handler");

    // top nav + footer + generic fallback
    private final By careersTopNav = By.linkText("Careers");
    private final By careersHref = By.cssSelector("a[href*='careers']");
    private final By careersFallback = By.xpath("//a[contains(translate(.,'CAREERS','careers'),'careers')]");

    public void open(String baseUrl) {
        driver.get(baseUrl);
    }

    public void acceptCookiesIfPresent() {
        if (isDisplayed(cookieAcceptBtn)) {
            click(cookieAcceptBtn);
        }
    }

    public void clickCareers() {
        if (isDisplayed(careersTopNav)) {
            click(careersTopNav);
            return;
        }
        if (isDisplayed(careersHref)) {
            click(careersHref);
            return;
        }
        click(careersFallback);
    }
}