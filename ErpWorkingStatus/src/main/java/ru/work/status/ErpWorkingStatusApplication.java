package ru.work.status;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErpWorkingStatusApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpWorkingStatusApplication.class, args);
    }

}
