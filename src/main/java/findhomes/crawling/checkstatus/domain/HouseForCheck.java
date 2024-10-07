package findhomes.crawling.checkstatus.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HouseForCheck {
    private Integer houseId; // Not NULL / 숫자8개
    private String url; // Not NULL
    private String status = "ACTIVE"; // Not NULL
    private LocalDateTime checkedAt; // Nullable
}
