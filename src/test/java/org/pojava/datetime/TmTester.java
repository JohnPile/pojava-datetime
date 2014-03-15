package org.pojava.datetime;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TmTester extends TestCase {

    private static final boolean DEBUG = false;

    @Override
    public void setUp() {
        DateTimeConfig.setGlobalDefault(null);
    }

    /**
     * Test that Tm returns same values produced by Calendar.
     */
    public void compareCalcs(long pointInTime) {
        // Use calendar to gather control values
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(pointInTime);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int millisecond = cal.get(Calendar.MILLISECOND);
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        // Verify that our values match.
        Tm tm = new Tm(pointInTime);
        assertEquals(year, tm.getYear());
        assertEquals(month, tm.getMonth() - 1);
        assertEquals(day, tm.getDay());
        assertEquals(hour, tm.getHour());
        assertEquals(minute, tm.getMinute());
        assertEquals(second, tm.getSecond());
        assertEquals(millisecond, tm.getMillisecond());
        assertEquals(millisecond * 1000000, tm.getNanosecond());
        assertEquals(dow, tm.getWeekday());
    }

    public void testCalcs() {
        compareCalcs(-12345);
        compareCalcs(0);
        compareCalcs(123456789);
    }

    public void testSpeed() {
        if (DEBUG) {
            long timer = System.currentTimeMillis();
            int iterations = 100000;
            for (int i = 0; i < iterations; i++) {
                Tm tm = new Tm(1234567890 + i * 100000000);
                int year = tm.getYear();
                int month = tm.getMonth();
                int day = tm.getDay();
                int hour = tm.getHour();
                int minute = tm.getMinute();
                int second = tm.getSecond();
                int millisecond = tm.getMillisecond();
                int nanosecond = tm.getNanosecond();
                int dow = tm.getWeekday();
            }
            long time1 = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                // The Calendar object is faster when reused, but
                // we're trying to simulate typical one-off usage.
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(1234567890 + i * 100000000);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                int second = cal.get(Calendar.SECOND);
                int millisecond = cal.get(Calendar.MILLISECOND);
                // We can only approximate this unsupported value
                int nanosecond = cal.get(Calendar.MILLISECOND) * 1000;
                int dow = cal.get(Calendar.DAY_OF_WEEK);
            }
            long time2 = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("Speed test: Pojava=");
            sb.append(time1 - timer);
            sb.append("ms, Calendar=");
            sb.append(time2 - time1);
            sb.append("ms.");
            System.out.println(sb.toString());
        }
    }

    /**
     * This should cover a broad spectrum of potential issues.
     */
    public void testFourYearsDaily() {
        DateTime dt = new DateTime("2008-01-01");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dt.toMillis());
        for (int i = 0; i < 365 * 4 + 1; i++) {
            Tm tm = new Tm(cal.getTimeInMillis());
            assertEquals(cal.get(Calendar.DATE), tm.getDay());
            assertEquals(1 + cal.get(Calendar.MONTH), tm.getMonth());
            assertEquals(cal.get(Calendar.YEAR), tm.getYear());
            assertEquals(cal.get(Calendar.HOUR), tm.getHour());
            assertEquals(cal.get(Calendar.MINUTE), tm.getMinute());
            assertEquals(cal.get(Calendar.SECOND), tm.getSecond());
            cal.add(Calendar.DATE, 1);
            cal.add(Calendar.SECOND, 1);
        }
    }

    public void testOldLeapDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1204272059000L); // Feb 29
        Tm tm = new Tm(cal.getTimeInMillis());
        assertEquals(cal.get(Calendar.DATE), tm.getDay());
        assertEquals(1 + cal.get(Calendar.MONTH), tm.getMonth());
        assertEquals(cal.get(Calendar.YEAR), tm.getYear());
        assertEquals(cal.get(Calendar.HOUR), tm.getHour());
        assertEquals(cal.get(Calendar.MINUTE), tm.getMinute());
        assertEquals(cal.get(Calendar.SECOND), tm.getSecond());

        cal.setTimeInMillis(1204358460000L); // Mar 1
        tm = new Tm(cal.getTimeInMillis());
        assertEquals(cal.get(Calendar.DATE), tm.getDay());
        assertEquals(1 + cal.get(Calendar.MONTH), tm.getMonth());
        assertEquals(cal.get(Calendar.YEAR), tm.getYear());
        assertEquals(cal.get(Calendar.HOUR), tm.getHour());
        assertEquals(cal.get(Calendar.MINUTE), tm.getMinute());
        assertEquals(cal.get(Calendar.SECOND), tm.getSecond());
    }

    /**
     * One significance of these two choices is that one of the TimeZone values observes DST, and the other doesn't.
     */
    public void testCalcDstSensitive() {
        calcDstSensitive(TimeZone.getTimeZone("EST"));
        calcDstSensitive(TimeZone.getTimeZone("PST"));
    }

    private void calcDstSensitive(TimeZone tz) {
        long DAY = 1000 * 86400;
        long WEEK = DAY * 7;
        int FEB = 1;
        SimpleDateFormat fmt = new SimpleDateFormat("HH");
        fmt.setTimeZone(tz);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        for (int yr = 1987; yr < 2950; yr++) {
            cal.set(yr, FEB, 28, 0, 0);
            long time = cal.getTimeInMillis();
            cal.setTimeInMillis(time);

            // Compare hours of two pre-DST dates exactly 7 24hr periods apart.
            assertEquals(fmt.format(new Date(time)), fmt.format(new Date(time + WEEK)));
            // Compare a pre-DST hour to a post-DST hour.
            if (tz.getDSTSavings() == 0) {
                assertEquals(fmt.format(new Date(time)), fmt.format(new Date(time + 6 * WEEK)));
            } else {
                assertTrue(!fmt.format(new Date(time)).equals(fmt.format(new Date(time + 6 * WEEK))));
            }

            // We should expect identical behavior from the Tm object.
            assertEquals(new Tm(time, tz).getHour(), new Tm(time + WEEK, tz).getHour());
            if (tz.getDSTSavings() == 0) {
                assertEquals((new Tm(time, tz).getHour()), new Tm(time + 6 * WEEK, tz).getHour());
            } else {
                assertTrue(new Tm(time, tz).getHour() != new Tm(time + 6 * WEEK, tz).getHour());
            }
        }
    }

    public void testCalcTime() {
        int yr = 0;
        StringBuffer sb = new StringBuffer();
        for (yr = 1000; yr <= 2110; yr++) {
            sb.setLength(0);
            sb.append(yr);
            sb.append("-01-01");
            long dtMillis = new DateTime(sb.toString()).toMillis();
            long tmMillis = Tm.calcTime(yr, 1, 1);
            assertEquals(dtMillis, tmMillis);
        }
    }

}
