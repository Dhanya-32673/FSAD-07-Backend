package com.electionmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ElectionMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElectionMonitorApplication.class, args);
    }
}
