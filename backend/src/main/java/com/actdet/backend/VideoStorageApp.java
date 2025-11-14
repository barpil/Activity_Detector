package com.actdet.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideoStorageApp {
    private static final Logger logger = LoggerFactory.getLogger(VideoStorageApp.class);


    public static void main(String[] args) {
        SpringApplication.run(VideoStorageApp.class, args);
        logger.info("Application has been initialized successfully.");
    }

}
