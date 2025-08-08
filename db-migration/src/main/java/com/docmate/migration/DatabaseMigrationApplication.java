package com.docmate.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class DatabaseMigrationApplication {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DatabaseMigrationApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("✅ Database migration completed successfully!");
        log.info("🚀 All services can now start safely with the complete database schema");

        // Exit the application after successful migration
        System.exit(0);
    }
}
