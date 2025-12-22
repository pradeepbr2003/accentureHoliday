package org.accenture.holiday.controller;

import org.accenture.holiday.model.Holiday;
import org.accenture.holiday.service.HolidayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller exposing endpoints to retrieve holiday information.
 *
 * <p>Base path: <code>/api/holidays</code></p>
 *
 * <p>The controller delegates to {@link HolidayService} to load holidays from a data file
 * whose path is provided via the application property {@code holidays.file.path}. If the property
 * is not set, it defaults to {@code src/main/resources/bangalore_holidays.log}.</p>
 */
@RestController
@RequestMapping("/api/holidays")

public class HolidayController {

    /** Service used to load holidays from the configured source. */
    private final HolidayService holidayService;
    /** Path to the holidays data file resolved from configuration. */
    private final Path holidaysFile;

    /**
     * Constructs a new {@code HolidayController}.
     *
     * @param holidayService   the service responsible for loading holiday data
     * @param holidaysFilePath path to the holidays data file, injected from {@code holidays.file.path};
     *                         defaults to {@code src/main/resources/bangalore_holidays.log} if not provided
     */
    public HolidayController(HolidayService holidayService,
                             @Value("${holidays.file.path:src/main/resources/bangalore_holidays.log}") String holidaysFilePath) {
        this.holidayService = holidayService;
        this.holidaysFile = Path.of(holidaysFilePath);
    }

    /**
     * Retrieves all mandatory holidays.
     *
     * @return HTTP 200 with a list of {@link Holiday} entries whose type is {@link Holiday.Type#MANDATORY}
     * @throws IOException if the holidays file cannot be read or parsed
     */
    @GetMapping("/mandatory")
    public ResponseEntity<List<Holiday>> getMandatoryHolidays() throws IOException {
        List<Holiday> holidays = holidayService.loadHolidays(holidaysFile).stream()
                .filter(h -> h.getType() == Holiday.Type.MANDATORY)
                .collect(Collectors.toList());
        return ResponseEntity.ok(holidays);
    }

    /**
     * Retrieves all floating holidays.
     *
     * @return HTTP 200 with a list of {@link Holiday} entries whose type is {@link Holiday.Type#FLOATING}
     * @throws IOException if the holidays file cannot be read or parsed
     */
    @GetMapping("/floating")
    public ResponseEntity<List<Holiday>> getFloatingHolidays() throws IOException {
        List<Holiday> holidays = holidayService.loadHolidays(holidaysFile).stream()
                .filter(h -> h.getType() == Holiday.Type.FLOATING)
                .collect(Collectors.toList());
        return ResponseEntity.ok(holidays);
    }

    /**
     * Retrieves mandatory holidays for a given month.
     *
     * @param month the month number (1–12)
     * @return HTTP 200 with a list of mandatory holidays in the specified month; an empty list if none match
     * @throws IOException if the holidays file cannot be read or parsed
     */
    @GetMapping("/mandatory/month/{month}")
    public ResponseEntity<List<Holiday>> getMandatoryHolidaysByMonth(@PathVariable int month) throws IOException {
        List<Holiday> holidays = holidayService.loadHolidays(holidaysFile).stream()
                .filter(h -> h.getType() == Holiday.Type.MANDATORY && h.getDate().getMonthValue() == month)
                .collect(Collectors.toList());
        return ResponseEntity.ok(holidays);
    }

    /**
     * Retrieves floating holidays for a given month.
     *
     * @param month the month number (1–12)
     * @return HTTP 200 with a list of floating holidays in the specified month; an empty list if none match
     * @throws IOException if the holidays file cannot be read or parsed
     */
    @GetMapping("/floating/month/{month}")
    public ResponseEntity<List<Holiday>> getFloatingHolidaysByMonth(@PathVariable int month) throws IOException {
        List<Holiday> holidays = holidayService.loadHolidays(holidaysFile).stream()
                .filter(h -> h.getType() == Holiday.Type.FLOATING && h.getDate().getMonthValue() == month)
                .collect(Collectors.toList());
        return ResponseEntity.ok(holidays);
    }

    /**
     * Searches for holidays by name using a case-insensitive substring match.
     *
     * @param keyword the text to search for within holiday names
     * @return HTTP 200 with a list of holidays whose names contain the {@code keyword}; may be empty if no matches
     * @throws IOException if the holidays file cannot be read or parsed
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Holiday>> searchHolidaysByName(@PathVariable String keyword) throws IOException {
        List<Holiday> holidays = holidayService.loadHolidays(holidaysFile).stream()
                .filter(h -> h.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(holidays);
    }
}

