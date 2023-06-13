package com.example.demo;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

@Log
@EnableAsync
@Transactional
@EnableScheduling
@SpringBootApplication
@SuppressWarnings("Duplicates")

public class EthioSwitchIncomingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EthioSwitchIncomingApplication.class, args);
    }

}
