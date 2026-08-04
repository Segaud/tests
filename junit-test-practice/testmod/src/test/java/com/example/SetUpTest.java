package com.example;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SetUpTest {
    
    WebDriver driver;
    WebDriverWait wait; 

    @BeforeEach
    void setUp() {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--incognito");
        driver = new ChromeDriver(opts);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void basicSearch() {
        driver.get("https://www.duckduckgo.com/");

        WebElement q = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
        q.sendKeys("selenium");
        q.sendKeys(Keys.ENTER);

        By result = By.cssSelector("[data-test-id='result'], h2 a, #links .result_title a");

        wait.until(ExpectedConditions.presenceOfElementLocated(result));

        Assertions.assertTrue(driver.getTitle().toLowerCase().contains("selenium"), "Should contain Selenium");
    }


}



















/*

browser automation toolkit

end to end tests

no static analysis

doesnt replace unit tests

small suite of tests focused on the highest value

web driver: controls language bindings.

selenium manager: manages drivers + downloads.


*/