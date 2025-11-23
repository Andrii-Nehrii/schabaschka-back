package schabaschka.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "schabaschka")
@EnableJpaRepositories(basePackages = "schabaschka")
@EntityScan(basePackages = "schabaschka")
public class SchabaschkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchabaschkaApplication.class, args);
    }
}
