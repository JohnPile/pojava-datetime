package org.pojava.datetime;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Implementation of CalendarSupplier that caches the Calendar in a
 * thread local variable.
 */
public final class ThreadLocalCalendarSupplier implements CalendarSupplier {
    private final ThreadLocal<Calendar> calendarThreadLocal = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    };

    @Override
    public Calendar getCalendar(TimeZone tz) {
        final Calendar calendar = calendarThreadLocal.get();
        calendar.clear();
        calendar.setTimeZone(tz);
        return calendar;
    }
}
