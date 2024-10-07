package findhomes.crawling.checkstatus;

import findhomes.crawling.checkstatus.domain.HouseForCheck;
import findhomes.crawling.crawling.Crawling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckService {
    public static final int PAGE_LIMIT = 50;
    public static final int MAX_WAIT_TIME = 5;
    public static final String CHECK_SELECTOR = ".fnzBWk h1";

    private final CheckRepository checkRepository;

    public List<HouseForCheck> getAllHouses() {
        return checkRepository.findAll();
    }

    public void check(int page) {
        while (true) {
            Crawling crawling = new Crawling()
                    .setDriverAtServer(false)
                    .setWaitTime(MAX_WAIT_TIME);
            // house page로 가져오기. 없으면 page 0부터 다시.
            List<HouseForCheck> houses = getHousesByPage(page);
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
                crawling.openUrlNewTab(house.getUrl());
                // check 요소가 없다면 = 매물이 삭제 or 비공개
                if (!crawling.waitForElementByCssSelector(CHECK_SELECTOR)) {
                    house.setStatus("DELETED");
                    log.info("[DELETED] house id:{}", house.getHouseId());
                }
                house.setCheckedAt(LocalDateTime.now());
                checkRepository.update(house);
            }
            crawling.quitDriver();
        }
    }

    public List<HouseForCheck> getHousesByPage(int page) {
        return checkRepository.findByPage(page, PAGE_LIMIT);
    }
}
