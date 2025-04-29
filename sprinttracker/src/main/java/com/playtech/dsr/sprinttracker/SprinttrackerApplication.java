package com.playtech.dsr.sprinttracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SprinttrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprinttrackerApplication.class, args);
    }

}
