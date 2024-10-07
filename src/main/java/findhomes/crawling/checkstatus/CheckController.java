package findhomes.crawling.checkstatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CheckController {

    private final CheckService checkService;

    @GetMapping("/status/update")
    public ResponseEntity<Void> statusUpdate(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer limit
    ) {
        checkService.check(page, limit);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("server ok");
    }
}
