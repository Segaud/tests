package com.example;

import java.time.Duration;
import java.util.NoSuchElementException;

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
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
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

    @Test
    void fluentWaitDemo() {
        driver.get("https://www.duckduckgo.com/");
        driver.findElement(By.name("q")).sendKeys("selenium");
        driver.findElement(By.name("q")).sendKeys(Keys.ENTER);

        By resultTitles = By.cssSelector("[data-test-id='result'], h2 a, #links .result_title a");

        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(NoSuchElementException.class); // skips the exception because empty is allowed.

        fluentWait.until(d -> d.findElements(resultTitles).size() >= 5);

        Assertions.assertTrue(driver.findElements(resultTitles).size() >= 5, "Should have at least 5");
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