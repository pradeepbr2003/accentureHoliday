package org.accenture.holiday.controller;

import org.accenture.holiday.model.Holiday;
import org.accenture.holiday.service.HolidayService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link HolidayController} verifying filtering and search behavior.
 */
class HolidayControllerTest {

    /**
     * Helper to create a {@link Holiday} with a computed day-of-week.
     */
    private Holiday createHoliday(String name, int year, int month, int day, String city, Holiday.Type type) {
        LocalDate date = LocalDate.of(year, month, day);
        return new Holiday(name, date, date.getDayOfWeek(), city, type);
    }

    /**
     * Ensures only mandatory holidays are returned by the endpoint.
     */
    @Test
    void returnsOnlyMandatoryHolidays() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        List<Holiday> data = List.of(
                createHoliday("Republic Day", 2024, 1, 15, "Bangalore", Holiday.Type.MANDATORY),
                createHoliday("Regional Festival", 2024, 1, 16, "Bangalore", Holiday.Type.FLOATING),
                createHoliday("Independence Day", 2024, 8, 15, "Bangalore", Holiday.Type.MANDATORY)
        );
        when(service.loadHolidays(any(Path.class))).thenReturn(data);

        List<Holiday> result = controller.getMandatoryHolidays().getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(h -> h.getType() == Holiday.Type.MANDATORY));
    }

    /**
     * Ensures only floating holidays are returned by the endpoint.
     */
    @Test
    void returnsOnlyFloatingHolidays() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        List<Holiday> data = List.of(
                createHoliday("Optional Festival", 2024, 4, 10, "Bangalore", Holiday.Type.FLOATING),
                createHoliday("Mandatory Event", 2024, 5, 1, "Bangalore", Holiday.Type.MANDATORY)
        );
        when(service.loadHolidays(any(Path.class))).thenReturn(data);

        List<Holiday> result = controller.getFloatingHolidays().getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Holiday.Type.FLOATING, result.get(0).getType());
        assertEquals("Optional Festival", result.get(0).getName());
    }

    /**
     * Verifies month filter for mandatory holidays.
     */
    @Test
    void returnsMandatoryHolidaysFilteredByMonth() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        List<Holiday> data = List.of(
                createHoliday("May Mandatory 1", 2024, 5, 2, "Bangalore", Holiday.Type.MANDATORY),
                createHoliday("June Mandatory", 2024, 6, 3, "Bangalore", Holiday.Type.MANDATORY),
                createHoliday("May Floating", 2024, 5, 4, "Bangalore", Holiday.Type.FLOATING)
        );
        when(service.loadHolidays(any(Path.class))).thenReturn(data);

        List<Holiday> result = controller.getMandatoryHolidaysByMonth(5).getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("May Mandatory 1", result.get(0).getName());
        assertEquals(5, result.get(0).getDate().getMonthValue());
        assertEquals(Holiday.Type.MANDATORY, result.get(0).getType());
    }

    /**
     * Verifies month filter for floating holidays.
     */
    @Test
    void returnsFloatingHolidaysFilteredByMonth() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        List<Holiday> data = List.of(
                createHoliday("July Floating 1", 2024, 7, 10, "Bangalore", Holiday.Type.FLOATING),
                createHoliday("July Floating 2", 2024, 7, 11, "Bangalore", Holiday.Type.FLOATING),
                createHoliday("July Mandatory", 2024, 7, 12, "Bangalore", Holiday.Type.MANDATORY),
                createHoliday("August Floating", 2024, 8, 13, "Bangalore", Holiday.Type.FLOATING)
        );
        when(service.loadHolidays(any(Path.class))).thenReturn(data);

        List<Holiday> result = controller.getFloatingHolidaysByMonth(7).getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(h -> h.getType() == Holiday.Type.FLOATING && h.getDate().getMonthValue() == 7));
    }

    /**
     * Ensures search is case-insensitive.
     */
    @Test
    void searchHolidaysByNameIsCaseInsensitive() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        List<Holiday> data = List.of(
                createHoliday("New Year", 2024, 1, 1, "Bangalore", Holiday.Type.MANDATORY),
                createHoliday("regional festival", 2024, 2, 2, "Bangalore", Holiday.Type.FLOATING),
                createHoliday("Company Day", 2024, 3, 3, "Bangalore", Holiday.Type.MANDATORY)
        );
        when(service.loadHolidays(any(Path.class))).thenReturn(data);

        List<Holiday> result = controller.searchHolidaysByName("FEST").getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("regional festival", result.get(0).getName());
    }

    /**
     * Invalid month should yield an empty result set.
     */
    @Test
    void returnsEmptyListForInvalidMonthParameter() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        when(service.loadHolidays(any(Path.class))).thenReturn(List.of(
                createHoliday("Event", 2024, 5, 5, "Bangalore", Holiday.Type.MANDATORY)
        ));

        List<Holiday> result = controller.getMandatoryHolidaysByMonth(13).getBody();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Search with no matches should return an empty list.
     */
    @Test
    void returnsEmptyListWhenNoSearchMatches() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        when(service.loadHolidays(any(Path.class))).thenReturn(List.of(
                createHoliday("Some Event", 2024, 1, 10, "Bangalore", Holiday.Type.MANDATORY)
        ));

        List<Holiday> result = controller.searchHolidaysByName("xyz").getBody();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Confirms IOExceptions from the service are propagated.
     */
    @Test
    void propagatesIOExceptionFromService() throws IOException {
        HolidayService service = mock(HolidayService.class);
        HolidayController controller = new HolidayController(service, "ignored/path.log");

        when(service.loadHolidays(any(Path.class))).thenThrow(new IOException("failed to read"));

        assertThrows(IOException.class, controller::getMandatoryHolidays);
    }
}
