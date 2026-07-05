package nus.iss.wellness.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling //for Jwt auto cleanup
public class WellnessApplication {
    public static void main(String[] args) {
        SpringApplication.run(WellnessApplication.class, args);
    }
}
