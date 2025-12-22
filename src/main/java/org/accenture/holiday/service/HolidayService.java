package org.accenture.holiday.service;

import org.accenture.holiday.model.Holiday;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service responsible for loading and parsing holiday data from a text file.
 *
 * <p>Each line is expected in the form:
 * {@code "Mandatory|Floating holiday for <Name> on <DayOfWeek>, <d-MMM-yyyy> in <City>"}.</p>
 *
 * <p>Malformed lines are ignored. Holidays that fall on Saturday or Sunday are filtered out.</p>
 */
@Service
public class HolidayService {

    /** Formatter for dates like {@code 15-Jan-2024}. */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.ENGLISH);
    /** Pattern capturing type, name, day-of-week token, date, and city from each line. */
    private static final Pattern LINE_PATTERN = Pattern.compile("^(Mandatory|Floating) holiday for (.+?) on (\\w+), (\\d{1,2}-[A-Za-z]{3}-\\d{4}) in (.+)$");

    /**
     * Parses a single line of the holidays file into a {@link Holiday}.
     *
     * @param line input line
     * @return a Holiday if the line matches the expected pattern; otherwise {@code null}
     */
    private static Holiday parseLine(String line) {
        Matcher matcher = LINE_PATTERN.matcher(line.trim());
        if (!matcher.matches()) {
            return null;
        }

        String typeToken = matcher.group(1);
        String name = matcher.group(2).trim();
        String dateStr = matcher.group(4).trim();
        String city = matcher.group(5).trim();

        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        Holiday.Type type = "Mandatory".equalsIgnoreCase(typeToken)
                ? Holiday.Type.MANDATORY
                : Holiday.Type.FLOATING;

        return new Holiday(name, date, dayOfWeek, city, type);
    }

    /**
     * Loads and parses holidays from the given file path.
     *
     * <p>Lines that do not match the expected pattern are skipped. Returned holidays exclude
     * those that fall on Saturday or Sunday.</p>
     *
     * @param filePath path to the text file containing holiday lines
     * @return list of parsed holidays (may be empty if no valid entries)
     * @throws IOException if reading the file fails
     */
    public List<Holiday> loadHolidays(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            return reader.lines()
                    .map(HolidayService::parseLine)
                    .filter(Objects::nonNull)
                    .filter(h -> h.getDayOfWeek() != DayOfWeek.SATURDAY && h.getDayOfWeek() != DayOfWeek.SUNDAY)
                    .collect(Collectors.toList());
        }
    }
}
