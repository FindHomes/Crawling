package findhomes.crawling.crawling;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

@Slf4j
@Getter
public class Crawling {
    private WebDriver driver = null;
    private WebDriverWait wait = null;
    private Actions actions = null;
    private WebElement preWaitingElement;
    private List<WebElement> preElements;
    private WebElement preElement;

    // driver 설정
    public Crawling setDriverAtServer(boolean isShowing) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("disable-gpu"); // GPU 비활성화 (일부 환경에서 필요)
        options.addArguments("no-sandbox"); // 샌드박스 비활성화
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 메모리 사용 비활성화
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36");
        // 이미지 로드 비활성화
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        prefs.put("profile.managed_default_content_settings.css", 2); // CSS 비활성화
        options.setExperimentalOption("prefs", prefs);

        if (!isShowing) {
            options.addArguments("headless=new");
        }

        this.driver = new ChromeDriver(options);

        return this;
    }

    public void openUrl(String url) {
        this.driver.get(url);
    }

    public void openUrlNewTab(String url, int waitMs) {
        // 현재 창의 핸들 저장
        String originalWindow = driver.getWindowHandle();
        // 새로운 탭 열기
        WebDriver newTab = driver.switchTo().newWindow(WindowType.TAB);
        // 새로운 탭에서 URL 열기
        newTab.get(url);
        // 기존 창 닫기
        driver.switchTo().window(originalWindow).close();
        // 기다리기
        try {
            Thread.sleep(waitMs);
        } catch (InterruptedException ignored) {

        }
        // 새로운 탭으로 포커스 이동 (탭이 2개 이상 있을 경우 마지막 탭으로 이동)
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
        }
    }

    public void closeDriver() {
        this.driver.close();
    }

    public void quitDriver() {
        this.driver.quit();
    }

    // action 설정
    public Crawling setAction() {
        if (driver == null) {
            System.out.println("error: driver 설정 안됐음.");
            throw new RuntimeException();
        }
        this.actions = new Actions(this.driver);
        return this;
    }

    // wait 시간(초) 설정
    public Crawling setWaitTime(int sec) {
        try {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(sec));
        } catch (TimeoutException e) {
            System.out.println("ERROR: 시간 초과 - " + e.getLocalizedMessage());
        }
        return this;
    }

    // 요소 기다리기 설정
    public WebElement getWaitingElementByCssSelector(String selector) {
        try {
            return this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
        } catch (NoSuchElementException e) {
            System.out.println("ERROR: No Element(Wait) Css: " + selector + " - " + e.getLocalizedMessage());
        }
        return null;
    }

    public boolean waitForElementByCssSelector(String selector) {
        try {
            this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    // iframe 변경하기
    public void changeIframe(String iframeId) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(iframeId)));
    }
}
