package org.accenture.holiday.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Immutable domain model representing a holiday entry parsed from the source file.
 *
 * <p>Each holiday includes a name, date, derived day-of-week, city where it applies,
 * and whether it is mandatory or floating.</p>
 */
public class Holiday {
    private final String name;
    private final LocalDate date;
    private final DayOfWeek dayOfWeek;
    private final String city;
    private final Type type;

    /**
     * Creates a new Holiday.
     *
     * @param name      human-readable name of the holiday
     * @param date      calendar date of the holiday
     * @param dayOfWeek day of week for the {@code date}
     * @param city      city or region to which the holiday applies
     * @param type      whether the holiday is mandatory or floating
     */
    public Holiday(String name, LocalDate date, DayOfWeek dayOfWeek, String city, Type type) {
        this.name = name;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.city = city;
        this.type = type;
    }

    /**
     * @return the holiday name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the holiday date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return the day of week corresponding to {@link #getDate()}
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @return the city or region associated with the holiday
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the holiday type (mandatory or floating)
     */
    public Type getType() {
        return type;
    }

    /**
     * Type of holiday supported by the system.
     */
    public enum Type {MANDATORY, FLOATING}
}

