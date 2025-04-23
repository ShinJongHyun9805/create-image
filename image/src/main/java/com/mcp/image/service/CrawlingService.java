package com.mcp.image.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    public void crawling() {

        WebDriver driver = new ChromeDriver();

        try {

            // WebDriver 셋업
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new"); // headless 모드 (UI 확인 시 주석처리)
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");

            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);

            // 크롤링 할 URL
            driver.get("https://www.miricanvas.com/template/all-types/%EC%9D%B4%EB%B2%88%EC%A3%BC%20%EC%9D%B8%EA%B8%B0%20%EC%B9%B4%EB%93%9C%EB%89%B4%EC%8A%A4?searchType=%EB%8D%94%EB%B3%B4%EA%B8%B0");

            List<WebElement> figures = driver.findElements(By.cssSelector("section figure"));

            for (int i = 0; i < 1; i++) {
                try {
                    // 클릭 전 DimWrapper 제거 시도
                    try {
                        WebElement dim = driver.findElement(By.cssSelector("div[data-f='DimWrapper-343d']"));
                        if (dim.isDisplayed()) {
                            actions.sendKeys(Keys.ESCAPE).perform();
                            Thread.sleep(500);
                        }
                    } catch (NoSuchElementException ne) {
                        log.error(ne.getMessage());

                        throw new TimeoutException(ne.getMessage());
                    }

                    List<WebElement> refreshedFigures = driver.findElements(By.cssSelector("section figure"));
                    WebElement figure = refreshedFigures.get(i);

                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", figure);
                    wait.until(ExpectedConditions.elementToBeClickable(figure)).click();

                    // modal_portal 등장 대기
                    WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id^='modal_portal_']")));

                    // 이미지 URL 수집
                    List<WebElement> images = modal.findElements(By.cssSelector("li img"));
                    System.out.println("==== Figure #" + (i + 1) + " ====");
                    for (WebElement img : images) {
                        String src = img.getAttribute("src");
                        System.out.println("Image URL: " + src);
                    }

                    // 키워드 수집
                    List<WebElement> keywords = modal.findElements(By.cssSelector("div[data-f='KeywordChipListDesktopWrapper-c76e'] > div[class*='sc-dQluUV']"));
                    for (WebElement keyword : keywords) {
                        System.out.println("Keyword: " + keyword.getText());
                    }

                    // 모달 닫기
                    actions.sendKeys(Keys.ESCAPE).perform();
                    Thread.sleep(500);

                } catch (TimeoutException e) {
                    System.out.println("Figure #" + (i + 1) + ": 모달 감지 실패, 넘어갑니다.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
