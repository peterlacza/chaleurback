package hu.elte.chaleur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChaleurApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChaleurApplication.class, args);
    }

}
