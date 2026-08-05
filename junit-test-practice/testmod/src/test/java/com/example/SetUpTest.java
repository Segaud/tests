package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SetUpTest {
    
    WebDriver driver;
    WebDriverWait wait; 

    static class DdgHomePage{
        private final WebDriver driver;
        private final WebDriverWait wait;
        private final By input = By.name("q"    );

        public DdgHomePage(WebDriver driver, WebDriverWait wait){
            this.driver = driver;
            this.wait = wait;
        }

        DdgHomePage open(){
            driver.get("https://www.duckduckgo.com/");
            wait.until(ExpectedConditions.elementToBeClickable(input));
            return this;
        }

        DdgResultsPage search(String query){
            WebElement q = driver.findElement(input);
            q.sendKeys(query);
            q.sendKeys(Keys.ENTER);
            return new DdgResultsPage(driver, wait);
        }
    }

    static class DdgResultsPage{
        private final WebDriver driver;
        private final WebDriverWait wait;
        private final By resultTitles = By.cssSelector("[data-test-id='result'], h2 a, #links .result_title a");

        public DdgResultsPage(WebDriver driver, WebDriverWait wait){
            this.driver = driver;
            this.wait = wait;
        }

        DdgResultsPage awaitLoaded(){
            wait.until(ExpectedConditions.presenceOfElementLocated(resultTitles));
            return this;
        }

        int resultCount(){
            return driver.findElements(resultTitles).size();
        }

        DdgResultsPage firstTitle(){
            var list = driver.findElements(resultTitles);
            return list.isEmpty() ? null : this;
        }
    }

    private void acceptCookiesIfDisplayed() {
        WebDriverWait cookiesWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
        By acceptAllButton = By.xpath(
            "//*[@id='L2AGLb'] | " +
            "//button[.//*[normalize-space()='Accept all']] | " +
            "//*[@role='button'][normalize-space()='Accept all']"
        );

        cookiesWait.until(
            ExpectedConditions.elementToBeClickable(acceptAllButton)
        ).click();

    } catch (TimeoutException ignored) {
        // The cookies may already have been accepted,
        // or the banner may not have appeared.
    }
    }

    private Path takeScreenshot(String screenshotName) throws IOException {
        Path dir = Paths.get("target", "screenshots");
        Files.createDirectories(dir);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmSS"));
        
        Path file = dir.resolve(screenshotName + "_" + ts + ".png");

        byte[] png = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);

        Files.write(file, png);

        return file;

    }

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

    @Test
    void searchKittens() {
        driver.get("https://duckduckgo.com/");

        acceptCookiesIfDisplayed();

        driver.findElement(By.name("q")).sendKeys("kittens");
        driver.findElement(By.name("q")).sendKeys(Keys.ENTER);

        By resultTitles = By.cssSelector("[data-test-id='result'], h2 a, #links .result_title a");

        wait.until(ExpectedConditions.presenceOfElementLocated(resultTitles));

        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(NoSuchElementException.class); // skips the exception because empty is allowed.

        fluentWait.until(d -> d.findElements(resultTitles).size() >= 5);

        Assertions.assertTrue(driver.getTitle().toLowerCase().contains("kittens"), "Should contain kittens");
    }

    @Test
    void actions() {
        driver.get("https://duckduckgo.com/");

        WebElement q = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));

        q.sendKeys("selenium");
        q.sendKeys(Keys.ENTER);

        By resultTitles = By.cssSelector("[data-test-id='result'], h2 a, #links .result_title a");

        WebElement firsElement = wait.until(ExpectedConditions.presenceOfElementLocated(resultTitles));

        new Actions(driver)
            .moveToElement(firsElement)
            .pause(200)
            .click()
            .perform();

        new WebDriverWait(driver, Duration.ofSeconds(12))
            .until(ExpectedConditions.urlContains("selenium"));

        driver.navigate().back();

        WebElement q2 = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));

        q2.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        q2.sendKeys(Keys.BACK_SPACE);
        q2.sendKeys("selenium");
        q2.sendKeys(Keys.ENTER);

        Assertions.assertTrue(driver.getTitle().toLowerCase().contains("selenium"), "Title should contain Selenium");

    }

    @Test
    void navigation() {
        driver.get("https://duckduckgo.com/");

        WebElement q = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));

        q.sendKeys("selenium");
        q.sendKeys(Keys.ENTER);

        By resultTitles = By.cssSelector("[data-test-id='result'], h2 a, #links .result_title a");

        WebElement first = wait.until(ExpectedConditions.elementToBeClickable(resultTitles));

        String resultURL = driver.getCurrentUrl();

        first.click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(resultURL)));
        String destURL = driver.getCurrentUrl();

        driver.navigate().back();
        wait.until(ExpectedConditions.urlToBe(resultURL));

        driver.navigate().forward();
        wait.until(ExpectedConditions.urlToBe(destURL));

        WebElement body = driver.findElement(By.tagName("body"));
        driver.navigate().refresh();

        wait.until(ExpectedConditions.stalenessOf(body)); // good indication that the body has been refreshed

        Assertions.assertTrue(driver.getTitle().toLowerCase().contains("selenium"), "title should contain selenium");


    }

    @Test
    void screenShot() throws IOException{
        driver.get("https://duckduckgo.com/");

        WebElement q = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));

        q.sendKeys("selenium");
        q.sendKeys(Keys.ENTER);

        By resultTitles = By.cssSelector("[data-test-id='result'], h2 a, #links .result_title a");

        WebElement first = wait.until(ExpectedConditions.elementToBeClickable(resultTitles));

        // dir + ts + resolve() + byte[] png + assert file exists

        Path dir = Paths.get("target", "screenshots");
        Files.createDirectories(dir);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmSS"));
        
        Path file = dir.resolve("ddg_results_screenshot" + ts + ".png");

        byte[] png = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);

        Files.write(file, png);

        Assertions.assertTrue(Files.exists(file), "Screenshot file should exist:" + file);

    

    }

    @Test
    void pageObjectModelTest(){
        // DdgHomePage home = new DdgHomePage(driver, wait);
        // DdgResultsPage results = home.open().search("selenium").awaitLoaded();
        // Assertions.assertTrue(results.resultCount() >= 5, "Should have at least 5 results");
        // Assertions.assertNotNull(results.firstTitle(), "First title should not be null");

        var results = new DdgHomePage(driver, wait)
            .open()
            .search("selenium")
            .awaitLoaded();

        Assertions.assertTrue(results.resultCount() > 0, "Should have at least 5 results");
    }

    @Test
    void exercise2() throws IOException {
        driver.get("https://automatetheboringstuff.com");

        WebElement c1 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='chapter1.html']")));

        String mainUrl = driver.getCurrentUrl();

        new Actions(driver)
        .scrollToElement(c1)
        .scrollByAmount(0, 400)
        .perform();

        // takeScreenshot("c1_wont_click");

        c1.click();

        wait.until(ExpectedConditions.urlContains("chapter1"));

        driver.navigate().back();
        wait.until(ExpectedConditions.urlToBe(mainUrl));

        WebElement c2 = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Chapter 2")));

        c2.click();

        wait.until(ExpectedConditions.urlContains("chapter2"));

        driver.navigate().back();
        wait.until(ExpectedConditions.urlToBe(mainUrl));

        WebElement c3 = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Chapter 3")));


        c3.click();

        wait.until(ExpectedConditions.urlContains("chapter3"));

        driver.navigate().back();
        wait.until(ExpectedConditions.urlToBe(mainUrl));


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