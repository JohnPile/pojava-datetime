package org.pojava.datetime;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Implementation of CalendarSupplier that create a new calendar every time.
 */
public final class DefaultCalendarSupplier implements CalendarSupplier {
    public static final DefaultCalendarSupplier INSTANCE = new DefaultCalendarSupplier();
    private DefaultCalendarSupplier() {
    }
    @Override
    public Calendar getCalendar(TimeZone tz) {
        return Calendar.getInstance(tz);
    }
}
