package org.pojava.datetime;


import java.util.Calendar;
import java.util.TimeZone;

/**
 * Defines how the calendar is created to calculate times.
 */
public interface CalendarSupplier {
    /**
     * return a cleared calendar with that timezone.
     * @param tz the timezone
     * @return the calendar
     */
    Calendar getCalendar(TimeZone tz);
}
