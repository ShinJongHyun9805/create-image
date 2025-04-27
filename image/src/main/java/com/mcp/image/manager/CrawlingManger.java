package com.mcp.image.manager;

import com.mcp.image.dto.res.CrawlingDataResponse;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class CrawlingManger {

    public List<CrawlingDataResponse> crawling() throws Exception {

        WebDriver driver = new ChromeDriver();

        // set response
        List<CrawlingDataResponse> crawlingDataListRes = new ArrayList<>();

        try {
            // Set WebDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");

            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);

            // 크롤링 할 URL
            driver.get("https://www.miricanvas.com/template/all-types/%EC%9D%B4%EB%B2%88%EC%A3%BC%20%EC%9D%B8%EA%B8%B0%20%EC%B9%B4%EB%93%9C%EB%89%B4%EC%8A%A4?searchType=%EB%8D%94%EB%B3%B4%EA%B8%B0");

            List<WebElement> figures = driver.findElements(By.cssSelector("section figure"));
            if (figures.isEmpty()) {
                log.error("해당 페이지에 figures 없음.");

                return new ArrayList<>();
            }

            for (int i = 0; i < 1; i++) {
                try {
                    CrawlingDataResponse crawlingDataRes = new CrawlingDataResponse();
                    crawlingDataRes.setRank(i + 1);

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
                    List<String> imageUrls = new ArrayList<>();
                    for (WebElement img : images) {
                        String src = img.getAttribute("src");

                        imageUrls.add(src);
                    }

                    crawlingDataRes.setImageUrls(imageUrls);

                    // 키워드 수집
                    List<WebElement> keywords = modal.findElements(By.cssSelector("div[data-f='KeywordChipListDesktopWrapper-c76e'] > div[class*='sc-dQluUV']"));

                    List<String> keywordList = new ArrayList<>();
                    for (WebElement keyword : keywords) {
                        keywordList.add(keyword.getText());
                    }

                    crawlingDataRes.setKeywords(keywordList);

                    crawlingDataListRes.add(crawlingDataRes);

                    // 모달 닫기
                    actions.sendKeys(Keys.ESCAPE).perform();
                    Thread.sleep(500);

                    // 랜덤 딜레이 (2~5초)
                    Random random = new Random();
                    int delay = random.nextInt(3000) + 2000;

                    Thread.sleep(delay);
                } catch (TimeoutException e) {
                    log.error("Figure # " + (i + 1) + ": 모달 감지 실패");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new Exception("크롤링 실패");
        } finally {
            driver.quit();
        }

        return crawlingDataListRes;
    }
}
