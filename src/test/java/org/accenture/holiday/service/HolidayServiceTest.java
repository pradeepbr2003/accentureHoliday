package org.accenture.holiday.service;

import org.accenture.holiday.model.Holiday;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HolidayService} covering parsing, filtering, and edge cases.
 */
class HolidayServiceTest {

    private final HolidayService service = new HolidayService();

    /**
     * Verifies both mandatory and floating weekday holidays are parsed from valid lines.
     */
    @Test
    void loadsWeekdayMandatoryAndFloating(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("holidays.log");
        Files.writeString(file, String.join("\n",
                "Mandatory holiday for Republic Day on Monday, 15-Jan-2024 in Bangalore",
                "Floating holiday for Regional Festival on Tuesday, 16-Jan-2024 in Bangalore"
        ));

        List<Holiday> holidays = service.loadHolidays(file);

        assertEquals(2, holidays.size());
        assertTrue(holidays.stream().anyMatch(h -> h.getName().equals("Republic Day") && h.getType() == Holiday.Type.MANDATORY));
        assertTrue(holidays.stream().anyMatch(h -> h.getName().equals("Regional Festival") && h.getType() == Holiday.Type.FLOATING));
    }

    /**
     * Ensures weekend holidays are filtered out.
     */
    @Test
    void skipsWeekendHolidays(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("holidays.log");
        Files.writeString(file, String.join("\n",
                "Mandatory holiday for Saturday Event on Saturday, 20-Jan-2024 in Bangalore",
                "Floating holiday for Sunday Event on Sunday, 21-Jan-2024 in Bangalore",
                "Mandatory holiday for Weekday Event on Monday, 22-Jan-2024 in Bangalore"
        ));

        List<Holiday> holidays = service.loadHolidays(file);

        assertEquals(1, holidays.size());
        assertEquals("Weekday Event", holidays.get(0).getName());
    }

    /**
     * Lines that do not conform to the expected pattern should be ignored.
     */
    @Test
    void ignoresMalformedLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("holidays.log");
        Files.writeString(file, String.join("\n",
                "Mandatory holiday for Valid Event on Monday, 15-Jan-2024 in Bangalore",
                "Not a valid line",
                "Another invalid line"
        ));

        List<Holiday> holidays = service.loadHolidays(file);

        assertEquals(1, holidays.size());
        assertEquals("Valid Event", holidays.get(0).getName());
    }

    /**
     * An empty file should result in an empty list of holidays.
     */
    @Test
    void returnsEmptyForEmptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("holidays.log");
        Files.writeString(file, "");

        List<Holiday> holidays = service.loadHolidays(file);

        assertTrue(holidays.isEmpty());
    }
}
