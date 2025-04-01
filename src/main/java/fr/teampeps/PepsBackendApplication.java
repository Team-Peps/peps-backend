package fr.teampeps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@SpringBootApplication
@EnableScheduling
public class PepsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PepsBackendApplication.class, args);
    }

}
