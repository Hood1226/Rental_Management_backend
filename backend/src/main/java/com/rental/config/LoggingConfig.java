package com.rental.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LoggingConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);
    private static final String LOG_DIR = "D:/RobinXavier/logs";
    
    @PostConstruct
    public void init() {
        try {
            File logDirectory = new File(LOG_DIR);
            if (!logDirectory.exists()) {
                boolean created = logDirectory.mkdirs();
                if (created) {
                    logger.info("Created log directory: {}", LOG_DIR);
                } else {
                    logger.warn("Failed to create log directory: {}", LOG_DIR);
                }
            } else {
                logger.debug("Log directory already exists: {}", LOG_DIR);
            }
        } catch (Exception e) {
            logger.error("Error initializing log directory: {}", e.getMessage(), e);
        }
    }
}

