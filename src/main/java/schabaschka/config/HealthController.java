package schabaschka.config;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping ("/api")
public class HealthController {
    @GetMapping("/health")
    public Map<String, Object> health(){
        Map<String, Object> body = new HashMap<>();
        body.put("status", "UP");
        body.put("time", Instant.now());
        return body;
    }

    @GetMapping("/health/ping")
    public String ping(){
        return "pong";
    }


}
