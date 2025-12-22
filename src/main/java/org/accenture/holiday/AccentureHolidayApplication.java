package org.accenture.holiday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Holiday service application.
 *
 * <p>This application exposes REST endpoints to query holidays and is
 * configured via standard Spring Boot mechanisms.</p>
 */
@SpringBootApplication
public class AccentureHolidayApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args optional command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AccentureHolidayApplication.class, args);
    }

}
