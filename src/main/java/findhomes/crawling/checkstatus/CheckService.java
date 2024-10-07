package findhomes.crawling.checkstatus;

import findhomes.crawling.checkstatus.domain.HouseForCheck;
import findhomes.crawling.crawling.Crawling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckService {
    public static final int MAX_WAIT_TIME = 5;
    public static final String CHECK_SELECTOR = ".fnzBWk h1";
    public static final String MORE_THAN_3 = ".jKiLYt";

    private final CheckRepository checkRepository;

    public List<HouseForCheck> getAllHouses() {
        return checkRepository.findAll();
    }

    public void check(int page, int limit) {
        while (true) {
            Crawling crawling = new Crawling()
                    .setDriverAtServer(false)
                    .setWaitTime(MAX_WAIT_TIME);
            // house page로 가져오기. 없으면 page 0부터 다시.
            List<HouseForCheck> houses = getHousesByPage(page, limit);
            page++;
            if (houses.isEmpty()) {
                page = 0;
                log.info("status check 1바퀴 완료");
                continue;
            }

            // 크롤링 하기
            for (HouseForCheck house : houses) {
                // 이미 status가 DELETED가 된건 넘어감
                if (house.getStatus().equals("DELETED")) {
                    continue;
                }
                try {
                    crawling.openUrlNewTab(house.getUrl(), 2000);
                } catch (Exception e) {
                    log.error("[[open url에서 오류]]", e);
                }
                // 3번 이상 본 매물 팝업 지우기
                try {
                    WebElement element = crawling.getDriver().findElement(By.cssSelector(MORE_THAN_3));
                    element.click();
                } catch (NoSuchElementException ignored) {

                } catch (Exception ignored) {

                }
                // check 요소가 없다면 = 매물이 삭제 or 비공개
                if (!crawling.waitForElementByCssSelector(CHECK_SELECTOR)) {
                    house.setStatus("DELETED");
                    log.info("[DELETED] house id:{}", house.getHouseId());
                }
                house.setCheckedAt(LocalDateTime.now());
                checkRepository.update(house);
            }
            crawling.quitDriver();
            try {
                Thread.sleep(10000); // 5000ms = 5초
            } catch (InterruptedException ignored) {

            }
        }
    }

    public List<HouseForCheck> getHousesByPage(int page, int limit) {
        return checkRepository.findByPage(page, limit);
    }
}
