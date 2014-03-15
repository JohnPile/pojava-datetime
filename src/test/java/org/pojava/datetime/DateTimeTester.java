package org.pojava.datetime;

import junit.framework.TestCase;
import org.pojava.datetime.examples.FixedTimeLocalConfig;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeTester extends TestCase {

    private final TimeZone localTz = TimeZone.getDefault();

    private DateTimeConfigBuilder configBuilder() {
        DateTimeConfigBuilder dtcBuilder = DateTimeConfigBuilder.newInstance();
        TimeZone tz = TimeZone.getDefault();
        dtcBuilder.setMonthMap(MonthMap.fromAllLocales());
        dtcBuilder.getTzMap().put("Z", "UTC");
        dtcBuilder.getTzCache().put(tz.getID(), tz);
        dtcBuilder.setDmyOrder(false);
        dtcBuilder.setInputTimeZone(TimeZone.getTimeZone("EST"));
        dtcBuilder.setOutputTimeZone(TimeZone.getTimeZone("EST"));
        return dtcBuilder;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Ensure that a different default TimeZone doesn't influence results.
        TimeZone.setDefault(TimeZone.getTimeZone("PST"));
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        IDateTimeConfig dtc = DateTimeConfig.getGlobalDefault();
        DateTime fakeSystemTime = new DateTime("2014-03-05", tz);
        FixedTimeLocalConfig config = FixedTimeLocalConfig.instanceOverridingTimeZones(
                dtc, tz, tz, fakeSystemTime.toMillis());
        DateTimeConfig.setGlobalDefault(config);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TimeZone.setDefault(localTz);
        DateTimeConfig.setGlobalDefault(null);
    }

    /**
     * Verifying fix of a bug reported as a patch in version 2.3.0
     */
    public void testSteveZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));
        DateTimeConfigBuilder builder = configBuilder();
        builder.setOutputTimeZone(TimeZone.getTimeZone("EST"));
        builder.setInputTimeZone(TimeZone.getTimeZone("EST"));
        DateTimeConfig.setGlobalDefault(DateTimeConfig.fromBuilder(builder));
        assertEquals("Thu Jan 02 00:00:00 EST 2003", DateTime.parse("01/02/03").toDate().toString());
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        assertEquals("Thu Jan 02 00:00:00 GMT 2003", DateTime.parse("01/02/03 GMT").toDate().toString());
    }

    /**
     * Test for the parsed ".987" in both millis and nanos.
     */
    public void testDateToMillis() {
        DateTime dt = DateTime.parse("2008-05-16 01:23:45.987 PST");
        assertEquals(987, dt.toMillis() % 1000);
        assertEquals(987000000, dt.getNanos());
    }

    /**
     * Truncate to any precision defined in CalendarUnit
     */
    public void testTruncate() {
        DateTime dt = DateTime.parse("2008-08-08 03:02:01.123456789");
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        assertEquals("2008-08-08 03:02:01", dt.truncate(CalendarUnit.NANOSECOND).toString());
        assertEquals("2008-08-08 03:02:01.123", dt.truncate(CalendarUnit.NANOSECOND).toString(format));
        assertEquals("2008-08-08 03:02:01.123", dt.truncate(CalendarUnit.MICROSECOND).toString(format));
        assertEquals("2008-08-08 03:02:01.123", dt.truncate(CalendarUnit.MILLISECOND).toString(format));
        assertEquals("2008-08-08 03:02:01.000", dt.truncate(CalendarUnit.SECOND).toString(format));
        assertEquals("2008-08-08 03:02:00.000", dt.truncate(CalendarUnit.MINUTE).toString(format));
        assertEquals("2008-08-08 03:00:00.000", dt.truncate(CalendarUnit.HOUR).toString(format));
        assertEquals("2008-08-08 00:00:00.000", dt.truncate(CalendarUnit.DAY).toString(format));
        assertEquals("2008-08-03 00:00:00.000", dt.truncate(CalendarUnit.WEEK).toString(format));
        assertEquals("2008-08-01 00:00:00.000", dt.truncate(CalendarUnit.MONTH).toString(format));
        assertEquals("2008-07-01 00:00:00.000", dt.truncate(CalendarUnit.QUARTER).toString(format));
        assertEquals("2008-01-01 00:00:00.000", dt.truncate(CalendarUnit.YEAR).toString(format));
        assertEquals("2000-01-01 00:00:00.000", dt.truncate(CalendarUnit.CENTURY).toString(format));
        dt = new DateTime(-22222222); // Eight twos, Brutus? 1969-12-31
        // 09:49:37.778 PST
        assertEquals("1969-12-31 12:49:37.778", dt.truncate(CalendarUnit.NANOSECOND).toString(format));
        assertEquals("1969-12-31 12:49:37.778", dt.truncate(CalendarUnit.MICROSECOND).toString(format));
        assertEquals("1969-12-31 12:49:37.778", dt.truncate(CalendarUnit.MILLISECOND).toString(format));
        assertEquals("1969-12-31 12:49:37.000", dt.truncate(CalendarUnit.SECOND).toString(format));
        assertEquals("1969-12-31 12:49:00.000", dt.truncate(CalendarUnit.MINUTE).toString(format));
        assertEquals("1969-12-31 12:00:00.000", dt.truncate(CalendarUnit.HOUR).toString(format));
        assertEquals("1969-12-31 00:00:00.000", dt.truncate(CalendarUnit.DAY).toString(format));
        assertEquals("1969-12-28 00:00:00.000", dt.truncate(CalendarUnit.WEEK).toString(format));
        assertEquals("1969-12-01 00:00:00.000", dt.truncate(CalendarUnit.MONTH).toString(format));
        assertEquals("1969-10-01 00:00:00.000", dt.truncate(CalendarUnit.QUARTER).toString(format));
        assertEquals("1969-01-01 00:00:00.000", dt.truncate(CalendarUnit.YEAR).toString(format));
        assertEquals("1900-01-01 00:00:00.000", dt.truncate(CalendarUnit.CENTURY).toString(format));
    }

    public void testToString() {
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        DateTime dt;
        // Date before epoch
        dt = DateTime.parse("1968-12-31 23:59:59.123456789");
        assertEquals("1968-12-31 23:59:59.123", dt.toString(format));
        dt = DateTime.parse("1945-03-09 23:42:59.123456789");
        assertEquals("1945-03-09 23:42:59.123", dt.toString(format));
    }

    /**
     * The Tm structure is another representation of time.
     */
    public void testTm() {

        Tm tm = new Tm(new DateTime("1965-06-30 03:04:05.6789"));
        assertEquals(1965, tm.getYear());
        assertEquals(6, tm.getMonth());
        assertEquals(30, tm.getDay());
        assertEquals(3, tm.getHour());
        assertEquals(4, tm.getMinute());
        assertEquals(5, tm.getSecond());
        assertEquals(678900000, tm.getNanosecond());

        tm = new Tm(new DateTime("2008-02-29 03:04:05.6789"));
        assertEquals(2008, tm.getYear());
        assertEquals(2, tm.getMonth());
        assertEquals(29, tm.getDay());
        assertEquals(3, tm.getHour());
        assertEquals(4, tm.getMinute());
        assertEquals(5, tm.getSecond());
        assertEquals(678900000, tm.getNanosecond());
    }

    /**
     * Adding Calendar Units shifts a date part, leaving others the same. Notably, it should adjust to compensate for Daylight
     * Saving Time.
     */
    public void testAddCalendarUnits() {
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        DateTime dt = DateTime.parse("1955-06-14 02:03:04.123456789");
        DateTime dtNew;
        dtNew = dt.add(CalendarUnit.NANOSECOND, 1);
        assertEquals(1, dtNew.getNanos() - dt.getNanos());
        dtNew = dt.add(CalendarUnit.MICROSECOND, -2);
        assertEquals(-2000, dtNew.getNanos() - dt.getNanos());
        dtNew = dt.add(CalendarUnit.MILLISECOND, 3);
        assertEquals(3000000, dtNew.getNanos() - dt.getNanos());
        dtNew = dt.add(CalendarUnit.SECOND, -4);
        assertEquals(-4 * Duration.SECOND, dtNew.toMillis() - dt.toMillis());
        dtNew = dt.add(CalendarUnit.MINUTE, 5);
        assertEquals(5 * Duration.MINUTE, dtNew.toMillis() - dt.toMillis());
        dtNew = dt.add(CalendarUnit.HOUR, -6);
        assertEquals(-6 * Duration.HOUR, dtNew.toMillis() - dt.toMillis());
        dtNew = dt.add(CalendarUnit.DAY, 7);
        assertEquals(7 * Duration.DAY, dtNew.toMillis() - dt.toMillis());
        dtNew = dt.add(CalendarUnit.WEEK, -8); // Crossing backwards from EDT to EST
        assertEquals(-8 * Duration.WEEK + Duration.HOUR, dtNew.toMillis() - dt.toMillis());
        dtNew = dt.add(CalendarUnit.MONTH, 9);
        assertEquals("1956-03-14 02:03:04.123", dtNew.toString(format));
        dtNew = dt.add(CalendarUnit.YEAR, -10);
        assertEquals("1945-06-14 02:03:04.123", dtNew.toString(format));
        dtNew = dt.add(CalendarUnit.CENTURY, 11);
        assertEquals("3055-06-14 02:03:04.123", dtNew.toString(format));
    }

    public void testLanguages() {
        // English
        assertEquals("1969-01-26 00:00:00", new DateTime("1969,1,26").toString());
        assertEquals("1945-03-09 00:00:00", new DateTime("March 9, 1945").toString());
        assertEquals("1996-02-03 00:00:00", new DateTime("03-feb-1996").toString());
        assertEquals("1776-07-04 00:00:00", new DateTime("This 4th day of July, 1776").toString());
        // French
        assertEquals("1789-06-20 00:00:00", new DateTime("20 juin, 1789").toString());
        assertEquals("1789-07-09 00:00:00", new DateTime("9 juillet, 1789").toString());
        // German
        assertEquals("1871-01-18 00:00:00", new DateTime("18, Januar 1871").toString());
        assertEquals("2008-12-25 00:00:00", new DateTime("25-Dez-2008").toString());
        // Spanish
        assertEquals("1821-08-24 00:00:00", new DateTime("24 agosto, 1821").toString());
        assertEquals("2000-02-29 00:00:00", new DateTime("el 29 de febrero de 2000").toString());
        // Italian
        assertEquals("1946-06-10 00:00:00", new DateTime("10 GIU, 1946").toString());
        assertEquals("2013-07-12 00:00:00", new DateTime("il 12 luglio del 2013").toString());
    }

    public void testLeap() {
        assertEquals("2000-02-29 00:00:00", new DateTime("2000-02-29 00:00:00").toString());
    }

    /**
     * These tests show the consistency of the interpretation of millisecond values between the native Date object and the
     * DateTime object.
     * <p/>
     * Dates represented in BCE (before 0001) by Date.toString() cannot be distinguished from similar dates in CE. Dates after
     * 9999 are addressable, but not interpreted as a year by DateTime.
     */
    public void testConsistency() {
        // The year 0001
        long millis = -62135700000000L;
        TimeZone.setDefault(DateTimeConfig.getGlobalDefault().getOutputTimeZone());
        Date d = new Date(millis);
        DateTime dt = new DateTime(d.toString());
        assertEquals(millis, dt.toMillis());

        // The year 9999
        millis = 253402260044000L;
        d = new Date(millis);
        dt = new DateTime(d.toString());
        assertEquals(millis, dt.toMillis());

        // A year after 9999
        millis = 263402260044000L;
        d = new Date(millis);
        dt = new DateTime(d.toString());
        assertEquals(millis, dt.toMillis());

        // A millisecond prior to the year 0001 EST
        millis = -62135751600001L;
        assertEquals(millis, new DateTime("0001-01-01 EST").toMillis() - 1);
        assertEquals("-0001-12-31 23:59:59", new DateTime(millis).toString());

    }

    /**
     * This tests behavior for leap year calculations.
     */
    public void testLeapYears() {
        // 100 year leap year exception
        assertEquals("1900-02-28 23:59:59", new DateTime("1900-03-01").add(-1).toString());
        // Regular year
        assertEquals("1902-02-28 23:59:59", new DateTime("1902-03-01").add(-1).toString());
    }

    public void testRegularYearLeap() {
        // Regular leap year
        assertEquals("1904-02-29 23:59:59", new DateTime("1904-03-01").add(-1).toString());
    }

    public void testThousandYearLeap() {
        // 1000 year leap year inclusion
        assertEquals("1000-02-29 23:59:59", new DateTime("1000-03-01").add(-1).toString());
    }

    public void testEdgeCases() {
        // Add Month to January 30
        assertEquals("2008-02-29 00:00:00", new DateTime("2008-01-30").add(CalendarUnit.MONTH, 1).toString());
    }

    /**
     * Test whether DateTime calculates the same day of week that the Calendar class does.
     */
    public void testDow() {
        long timer = System.currentTimeMillis();
        TimeZone.setDefault(DateTimeConfig.getGlobalDefault().getOutputTimeZone());
        Tm tm = new Tm(timer);
        Calendar cal = Calendar.getInstance();
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        assertEquals(dow, tm.getWeekday());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        assertEquals(Calendar.SUNDAY, new DateTime(cal.getTimeInMillis()).weekday());
        TimeZone tz = TimeZone.getTimeZone("PST");
        assertEquals(Calendar.WEDNESDAY, new DateTime(0, tz).weekday());
        assertEquals(Calendar.THURSDAY, new DateTime(-40 * Duration.WEEK + Duration.DAY, tz).weekday());
    }

    public void testCompareTo() {
        DateTime dt1 = new DateTime(12345);
        DateTime dt2 = new DateTime(12346);
        assertTrue(dt1.compareTo(dt2) < 0);
    }

    public void testRelativeDateMinus() {
        long start = System.currentTimeMillis();
        DateTime dt1 = new DateTime();
        DateTime dt2 = new DateTime("-1");
        /*
         * System.out.println("dt1=" + dt1.toString()); System.out.println("dt2=" + dt2.toString()); System.out.println("dt2+DAY="
         * + dt2.add(Duration.DAY).toString()); System.out.println("dt2.toMillis=" + dt2.toMillis());
         * System.out.println("dt2.add(DAY).toMillis=" + dt2.add(Duration.DAY).toMillis());
         */
        long dur = System.currentTimeMillis() - start;
        long diff = dt2.add(Duration.DAY).toMillis() - dt1.toMillis();
        /*
         * System.out.println("dur=" + dur); System.out.println("diff=" + diff);
         */
        // The relative date "-1" represents 24hrs in past.
        // The values for dt1 and dt2 will be one day apart, plus some
        // small bit of extra time that elapsed between the two calculations.
        assertTrue(diff <= dur);
    }

    public void testRelativeDatePlusD() {
        long start = System.currentTimeMillis();
        DateTime dt1 = new DateTime();
        DateTime dt2 = new DateTime("+1D");
        long dur = System.currentTimeMillis() - start;
        long diff = dt2.add(-Duration.DAY).toMillis() - dt1.toMillis();
        // The relative date "-1" represents 24hrs in past.
        // The values for dt1 and dt2 will be one day apart, plus some
        // small bit of extra time that elapsed between the two calculations.
        assertTrue(diff <= dur);
    }

    public void testRelativeDateMinusD() {
        long start = System.currentTimeMillis();
        DateTime dt1 = new DateTime();
        DateTime dt2 = new DateTime("-2D");
        long dur = System.currentTimeMillis() - start;
        long diff = dt2.add(-2 * Duration.DAY).toMillis() - dt1.toMillis();
        // The relative date "-2D" represents 48hrs in past.
        // The values for dt1 and dt2 will be one day apart, plus some
        // small bit of extra time that elapsed between the two calculations.
        assertTrue(diff <= dur);
    }

    /**
     * It is always prudent to verify that your examples actually work :)
     */
    public void testJavaDocClaims() {
        DateTime dt1 = new DateTime("3:21pm on January 26, 1969");
        DateTime dt2 = new DateTime("26-Jan-1969 03:21 PM");
        DateTime dt3 = new DateTime("1/26/69 15:21");
        DateTime dt4 = new DateTime("1969.01.26 15.21");
        DateTime dt5 = new DateTime("el 26 de enero de 1969 15.21");
        assertEquals(dt1, dt2);
        assertEquals(dt1, dt3);
        assertEquals(dt1, dt4);
        assertEquals(dt1, dt5);
    }

    /**
     * This looks for a broad spectrum of issues, spanning different times of day, days of the month, leap and non-leap years.
     */
    public void testFourYearsDaily() {
        IDateTimeConfig config = DateTimeConfig.getGlobalDefault();
        TimeZone.setDefault(config.getInputTimeZone());
        DateTime dt = new DateTime("2008-01-01");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dt.toMillis());
        for (int i = 0; i < 365 * 4; i++) {
            assertEquals(cal.getTimeInMillis(), dt.toMillis());
            cal.add(Calendar.DATE, 1);
            cal.add(Calendar.SECOND, 61);
            dt = dt.add(CalendarUnit.DAY, 1).add(CalendarUnit.SECOND, 61);
        }
    }

    public void test1600() {
        DateTime leap = new DateTime("1200-03-01");
        assertEquals("1200-03-01 00:00:00", leap.toString());
        leap = new DateTime("1604-03-01");
        assertEquals("1604-03-01 00:00:00", leap.toString());
        leap = new DateTime("1600-03-01");
        assertEquals("1600-03-01 00:00:00", leap.toString());
    }

    public void testThousandEdges() {
        Calendar calFeb = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        for (int year = 1000; year <= 2110; year++) {
            DateTime dtMar = new DateTime(Integer.toString(year) + "-03-01");
            DateTime dtFeb = dtMar.add(-1);
            assertEquals("-03-01", dtMar.toString().substring(4, 10));
            calFeb.setTimeInMillis(dtFeb.toMillis());
            int dom = calFeb.get(Calendar.DAY_OF_MONTH);
            int month = calFeb.get(Calendar.MONTH) + 1;
            sb.setLength(0);
            sb.append(year);
            sb.append("-0");
            sb.append(month);
            sb.append("-");
            sb.append(dom);
            sb.append(" 23:59:59");
            assertEquals(sb.toString(), dtFeb.toString());
        }
    }

    /**
     * A European formatted date presents day before month, where North America presents month before day.
     */
    public void testEuropean() {
        DateTimeConfigBuilder builder = configBuilder();
        builder.setDmyOrder(true);
        DateTimeConfig.setGlobalDefault(DateTimeConfig.fromBuilder(builder));
        DateTime dt1 = new DateTime("01/02/2003");
        assertEquals("2003-02-01 00:00:00", dt1.toString());
        dt1 = new DateTime("12/11/2010 09:08:07");
        assertEquals("2010-11-12 09:08:07", dt1.toString());
        dt1 = new DateTime("20080109");
        assertEquals("2008-01-09 00:00:00", dt1.toString());
    }

    public void testEuropean2() {
        DateTimeConfigBuilder builder = configBuilder();
        builder.setDmyOrder(true);
        DateTimeConfig.setGlobalDefaultFromBuilder(builder);
        DateTime dt1 = new DateTime("01-07-2003");
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));
        String str = dt1.toDate().toString();
        assertEquals("Tue Jul 01 00:00:00", str.subSequence(0, 19));
    }

    public void testPacked() {
        DateTime dt1 = new DateTime("20080109");
        assertEquals("2008-01-09 00:00:00", dt1.toString());
    }

    public void testFormat() {
        DateTime dt = new DateTime("2010-02-14 03:00 EST");
        assertEquals("-05:00 AD -0500 EST", dt.toString("ZZ G Z z"));
    }

    public void testShift() {
        Shift shift = new Shift("P5HT7M31S");
        DateTime dt = new DateTime("2010-02-14 03:00 EST").shift(shift);
        assertEquals("2010-02-14 08:07:31", dt.toString());
    }

    public void testShift2() {
        DateTime dt = new DateTime("2011-03-04").shift(new Shift("1Y1M1W1D"));
        assertEquals("2012", dt.toString("yyyy"));
        assertEquals("04", dt.toString("MM"));
        assertEquals("12", dt.toString("dd"));
    }

    public void testShift3() {
        DateTime dt = new DateTime("2010-02-14 03:00 EST").shift("P5HT7M31S");
        assertEquals("2010-02-14 08:07:31", dt.toString());
    }

    public void testNearDST() {
        // EST observed on the 2nd Sunday in March
        DateTime dt = new DateTime("2009-03-08 00:00 PST");
        dt = dt.add(Duration.HOUR * -2);
        for (int i = 0; i < 8; i++) {
            // System.out.println(dt + " - " + dt.toLocalString());
            dt = dt.add(Duration.HOUR);
        }
    }

    public void testNonsenseMonth() {
        try {
            DateTime dt = new DateTime("2010-02-00");
            fail("Expected IllegalArgumentException, not " + dt);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.toString().contains("Invalid day parsed from [0]."));
        }
    }

    public void testNonsenseDay() {
        try {
            DateTime dt = new DateTime("2010-01-32");
            fail("Expected IllegalArgumentException, not " + dt);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.toString().contains("Invalid day parsed from [32]"));
        }
    }

    public void testNonsenseHour() {
        try {
            DateTime dt = new DateTime("17-Aug-2010 30:20:10");
            fail("Expected IllegalArgumentException, not " + dt);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.toString().contains("Invalid hour parsed from [30]."));
        }
    }

    public void testNonsenseMinute() {
        try {
            DateTime dt = new DateTime("2010.04.30 8:61");
            fail("Expected IllegalArgumentException, not " + dt);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.toString().contains("Invalid minute parsed from [61]."));
        }
    }

    public void testNonsenseSecond() {
        try {
            DateTime dt = new DateTime("20-Sep-2010 8:7:77");
            fail("Expected IllegalArgumentException, not " + dt);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.toString().contains("Invalid second parsed from [77]."));
        }
    }

    public void testMissingYear() {
        try {
            DateTime dt = new DateTime("20-Sep");
            fail("Expected IllegalArgumentException, not " + dt);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.toString().contains("Could not determine Year, Month, and Day"));
        }
    }

    /**
     * Generate a bunch of dates of the format YYYY-MM-DD, where some are known to have invalid values, such as 0 or 13 for a
     * month, 0 or 32 for a day. Detect the validity of the dates by regex, then verify whether DateTime throws an exception when
     * it should, and captures a valid date when it should.
     */
    public void testBruteForceYYYYMMDD() {
        DateTime dt;
        for (int yr = 1990; yr < 2010; yr++) {
            for (int mo = 0; mo <= 13; mo++) {
                for (int da = 0; da <= 32; da++) {
                    String strDate = yr + "-" + mo + "-" + da;
                    if (strDate.matches("^[0-9]{4}-([1-9]|1[0-2])-([1-9]|[1-2][0-9]|3[01])$")) {
                        if (strDate.matches("^[0-9]{4}-([469]|11)-31$")) {
                            // Invalid Date
                            try {
                                new DateTime(strDate);
                                fail("Expected IllegalArgumentException in A.");
                            } catch (IllegalArgumentException ex) {
                                // System.out.println("A. " + strDate + " : " + ex.toString());
                            }
                        } else if (strDate.matches("^[0-9]{4}-2-3[01]$")) {
                            // Invalid Date
                            try {
                                new DateTime(strDate);
                                fail("Expected IllegalArgumentException in B.");
                            } catch (IllegalArgumentException ex) {
                                // System.out.println("B. " + strDate + " : " + ex.toString());
                            }
                        } else if (yr % 4 != 0 && strDate.matches("^[0-9]{4}-2-29")) {
                            // Invalid Date
                            try {
                                new DateTime(strDate);
                                fail("Expected IllegalArgumentException in C.");
                            } catch (IllegalArgumentException ex) {
                                // System.out.println("C. " + strDate + " : " + ex.toString());
                            }
                        } else {
                            // Valid Date
                            new DateTime(strDate);
                        }
                    } else {
                        // Invalid Date
                        try {
                            dt = new DateTime(strDate);
                            fail("Expected IllegalArgumentException in D from " + strDate + ", not " + dt.toString());
                        } catch (IllegalArgumentException ex) {
                            // System.out.println("D. " + strDate + " : " + ex.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * Problem statement contributed by sereende, bug fixed in version 2.5.0
     *
     * @throws Exception
     */
    public void testPuncDate() throws Exception {
        DateTime dt = new DateTime("August 30, 2010?");
        assertNotNull(dt);
    }

    /**
     * Problem statement contributed by pstanar, bug fixed in version 2.5.0
     *
     * @throws Exception
     */
    public void testZeroYear() throws Exception {
        try {
            DateTime dt = new DateTime("0000-01-02");
            fail("Year is 1+ BC or 1+ AD, but never 0.  DateTime parsed: " + dt);
        } catch (IllegalArgumentException ex) {
            // good
        }
    }

    /**
     * Detect time before date.
     *
     * @throws Exception
     */
    public void testTimeFirst() throws Exception {
        DateTime dt = new DateTime("2:53 pm, January 26, 1969");
        assertEquals("1969-01-26 14:53:00", dt.toString());
    }

    /**
     * Support accent aigu on d�c and f�v.
     *
     * @throws Exception
     */
    public void testAccentAigu() throws Exception {
        DateTime dt = new DateTime("30-DÉC.-2009 11:47:19");
        assertEquals("2009-12-30 11:47:19", dt.toString());
        dt = new DateTime("30-déc.-2009 11:47:19");
        assertEquals("2009-12-30 11:47:19", dt.toString());
        dt = new DateTime("28-fév.-2010 11:47:19");
        assertEquals("2010-02-28 11:47:19", dt.toString());
        String s = "28-f" + '\u00c9' + "v.-2010 11:47:19";
        dt = new DateTime(s);
        assertEquals("2010-02-28 11:47:19", dt.toString());
    }

    public void test12am() throws Exception {
        DateTime dt = new DateTime("06-11-2009, 12:01 am");
        assertEquals("2009-06-11 00:01:00", dt.toString());
        dt = new DateTime("06-11-2009, 12:01:01 am");
        assertEquals("2009-06-11 00:01:01", dt.toString());
        dt = new DateTime("06-11-2009, 12:01:01.002 am");
        assertEquals("2009-06-11 00:01:01", dt.toString());
        dt = new DateTime("06-11-2009, 12:01:01am");
        assertEquals("2009-06-11 00:01:01", dt.toString());
        dt = new DateTime("06-11-2009, 12:01:01.002am");
        assertEquals("2009-06-11 00:01:01", dt.toString());
        dt = new DateTime("06-11-2009, 12:01am");
        assertEquals("2009-06-11 00:01:00", dt.toString());
        dt = new DateTime("06-11-2009, 12AM");
        assertEquals("2009-06-11 00:00:00", dt.toString());
    }

    public void testNoYear() throws Exception {
        DateTime dt;
        dt = new DateTime("06 Aug 01:23:45");
        String year = new DateTime().toString("yyyy");
        assertEquals(year + "-08-06 01:23:45", dt.toString());
    }

    /**
     * Verify fix of bug discovered by Mike Smith Parsing of dates with T separator.
     *
     * @since 2.6.1
     */
    public void testTSeparator() throws Exception {
        String dateTimeStringIn = "1969-01-20T18:00:03.928223333";
        String newPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        DateTime parsedDateString = new DateTime(dateTimeStringIn);
        String outputDateTime = DateTimeFormat.format(newPattern, parsedDateString);
        assertEquals("1969-01-20T18:00:03.928Z", outputDateTime);
    }

    public void testCrammedZ() throws Exception {
        String dts1 = "1969-01-20T18:00:03.928Z";
        String dts2 = "1969-01-20 18:00:03.928 Z";
        DateTime dt1 = new DateTime(dts1);
        DateTime dt2 = new DateTime(dts2);
        assertEquals(dt1, dt2);
    }

    public void testCrammedOther() throws Exception {
        String dts1 = "27AUG2011PST";
        String dts2 = "27-AUG-2011 PST";
        DateTime dt1 = new DateTime(dts1);
        DateTime dt2 = new DateTime(dts2);
        assertEquals(dt1, dt2);
    }

    public void testDayString() throws Exception {
        DateTime dt = new DateTime("2011-09-01 12:00 PST");
        assertEquals("Thursday", dt.toString("EEEE"));
        assertEquals("Friday", dt.add(CalendarUnit.DAY, 1).toString("EEEE"));
        assertEquals("Saturday", dt.add(CalendarUnit.DAY, 2).toString("EEEE"));
        assertEquals("Sunday", dt.add(CalendarUnit.DAY, 3).toString("EEEE"));
        assertEquals("Monday", dt.add(CalendarUnit.DAY, 4).toString("EEEE"));
        assertEquals("Tuesday", dt.add(CalendarUnit.DAY, 5).toString("EEEE"));
        assertEquals("Wednesday", dt.add(CalendarUnit.DAY, 6).toString("EEEE"));
        assertEquals("Thu", dt.toString("E"));
        assertEquals("Fri", dt.add(CalendarUnit.DAY, 1).toString("E"));
        assertEquals("Sat", dt.add(CalendarUnit.DAY, 2).toString("E"));
        assertEquals("Sun", dt.add(CalendarUnit.DAY, 3).toString("E"));
        assertEquals("Mon", dt.add(CalendarUnit.DAY, 4).toString("E"));
        assertEquals("Tue", dt.add(CalendarUnit.DAY, 5).toString("E"));
        assertEquals("Wed", dt.add(CalendarUnit.DAY, 6).toString("E"));
    }

    public void testDocs() throws Exception {
        String sample = "2011-12-13T14:15:16 PST";
        DateTime dt = new DateTime(sample);
        assertEquals("2011-12-13 14:15:16 PST", dt.toString("yyyy-MM-dd HH:mm:ss z", TimeZone.getDefault()));
    }

    public void testConstructorMT() {
        DateTime dt = new DateTime(0, "GMT");
        assertEquals(0, dt.getSeconds());
    }

    public void testConstructorSN() {
        DateTime dt = new DateTime(123, 456);
        assertEquals(123, dt.getSeconds());
        assertEquals(456, dt.getNanos());
    }

    public void testConstructorSNT() {
        DateTime dt = new DateTime(123, 456, TimeZone.getTimeZone("IST"));
        assertEquals(123, dt.getSeconds());
        assertEquals(456, dt.getNanos());
        assertEquals("IST", dt.toString("z"));
    }

    public void testConstructorSNTs() {
        DateTime dt = new DateTime(123, 456, "IST");
        assertEquals(123, dt.getSeconds());
        assertEquals(456, dt.getNanos());
        assertEquals("IST", dt.toString("z"));
    }

    public void testConstructorNullStr() {
        IDateTimeConfig dtc = DateTimeConfig.getGlobalDefault();
        long start = dtc.systemTime();
        String nullString = null;
        DateTime dt = new DateTime(nullString);
        long end = dtc.systemTime();
        assertTrue(start / 1000 <= dt.getSeconds());
        assertTrue(1 + end / 1000 >= dt.getSeconds());
    }

    public void testConstructorNullStrI() {
        long start = System.currentTimeMillis();
        String nullString = null;
        DateTimeConfigBuilder builder = configBuilder();
        DateTimeConfig dtc = DateTimeConfig.fromBuilder(builder);
        DateTime dt = new DateTime(nullString, dtc);
        long end = System.currentTimeMillis();
        assertTrue(start / 1000 <= dt.getSeconds());
        assertTrue(1 + end / 1000 >= dt.getSeconds());
    }

    public void testConstructorTs() {
        Timestamp ts = new Timestamp(12345);
        ts.setNanos(233322235);
        DateTime dt = new DateTime(ts);
        assertEquals(12, dt.getSeconds());
        assertEquals(233322235, dt.getNanos());
        assertEquals(ts, dt.toTimestamp());
    }

    public void testCompareToNull() {
        DateTime dt = new DateTime();
        try {
            DateTime dtNull = null;
            dt.compareTo(dtNull);
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            assertTrue(ex.getMessage().contains("to null"));
        }
    }

    public void testToStringTz() {
        DateTime dt = new DateTime("2020-02-20 15:30 GMT");
        assertEquals("2020-02-20 21:00:00", dt.toString(TimeZone.getTimeZone("IST")));
    }

    public void testToStringFmtTzLoc() {
        DateTime dt = new DateTime("2020-02-20 15:30 GMT");
        String fmt = "yyyy MMM Z";
        TimeZone tz = TimeZone.getTimeZone("IST");
        Locale loc = Locale.FRANCE;
        assertEquals("2020 févr. +0530", dt.toString(fmt, tz, loc));
    }

    public void testParseYYYYMMDD() {
        DateTime dt1 = new DateTime("2011-03-28");
        DateTime dt2 = new DateTime("20110328");
        assertEquals(dt1, dt2);
    }

    public void testRelativeYearMinus() {
        DateTime dt1 = new DateTime();
        DateTime dt2 = new DateTime("-1Y");
        Tm tm1 = new Tm(dt1);
        Tm tm2 = new Tm(dt2);
        assertEquals(tm1.getYear(), tm2.getYear() + 1);
    }

    public void testRelativeYearPlus() {
        DateTime dt1 = new DateTime();
        DateTime dt2 = new DateTime("+1Y");
        Tm tm1 = new Tm(dt1);
        Tm tm2 = new Tm(dt2);
        assertEquals(tm1.getYear(), tm2.getYear() - 1);
    }

    public void testRelativeYearBad() {
        try {
            new DateTime("-1Y2");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("not parse"));
        }
    }

    public void testRelativeMonthMinus() {
        DateTime dt1 = new DateTime();
        DateTime dt2 = new DateTime("-1M");
        Tm tm1 = new Tm(dt1);
        Tm tm2 = new Tm(dt2);
        if (tm1.getMonth() == 1) {
            assertEquals(12, tm2.getMonth());
        } else {
            assertEquals(tm1.getMonth(), tm2.getMonth() + 1);
        }
    }

    public void testRelativeMonthPlus() {
        DateTime dt1 = new DateTime();
        DateTime dt2 = new DateTime("+1M");
        Tm tm1 = new Tm(dt1);
        Tm tm2 = new Tm(dt2);
        if (tm1.getMonth() == 12) {
            assertEquals(1, tm2.getMonth());
        } else {
            assertEquals(tm1.getMonth(), tm2.getMonth() - 1);
        }
    }

    public void testConstructorNullWithConfig() {
        IDateTimeConfig dtc = DateTimeConfig.getGlobalDefault();
        String str = null;
        long before = dtc.systemTime() / 1000;
        DateTime dt = new DateTime(str, dtc);
        long after = dtc.systemTime() / 1000;
        assertTrue(before <= dt.getSeconds());
        assertTrue(after >= dt.getSeconds());
    }

    public void testConstructorBlank() {
        try {
            new DateTime("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("Cannot parse"));
        }
    }

    public void testConstructorBlankConfig() {
        try {
            DateTimeConfig dtc = null;
            new DateTime("", dtc);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("Cannot parse"));
        }
    }

    public void testParseNull() {
        IDateTimeConfig dtc = DateTimeConfig.getGlobalDefault();
        String str = null;
        long before = dtc.systemTime() / 1000;
        DateTime dt1 = DateTime.parse(str);
        DateTime dt2 = DateTime.parse(str, dtc);
        long after = dtc.systemTime() / 1000;
        assertTrue(before <= dt1.getSeconds());
        assertTrue(before <= dt2.getSeconds());
        assertTrue(after >= dt1.getSeconds());
        assertTrue(after >= dt2.getSeconds());
    }

    public void testHashCode() {
        DateTime dt = new DateTime();
        assertTrue(0 != dt.hashCode());
    }

    public void testHourlyOffsetWithColon() {
        DateTime dt = DateTime.parse("2012-01-02 17:30+05:30"); // 5:30pm in India(GMT+5.5), Noon UTC, 7am EST(-5), 4am PST(-8)
        assertEquals("2012-01-02 07:00:00", dt.toString()); // DateTimeConfig.outputTimeZone is set to EST
        assertEquals("2012-01-02 04:00:00", dt.toString(TimeZone.getTimeZone("America/Los_Angeles")));
    }

    public void testHourlyOffsetWithoutColon() {
        DateTime dt = DateTime.parse("2012-01-02 17:30+0530"); // 5:30pm in India(GMT+5.5), Noon UTC, 7am EST(-5), 4am PST(-8)
        assertEquals("2012-01-02 07:00:00", dt.toString()); // DateTimeConfig.outputTimeZone is set to EST
        assertEquals("2012-01-02 04:00:00", dt.toString(TimeZone.getTimeZone("America/Los_Angeles")));
    }

    public void testInvalidDates() {
        try {
            DateTime dt = DateTime.parse("31/50/2013 00:30:05");
            fail("Expected IllegalArgumentException in MDY order, but got " + dt.toString());
        } catch (IllegalArgumentException ex) {
            // expected
        }
        DateTimeConfigBuilder builder = configBuilder();
        builder.setDmyOrder(true);
        DateTimeConfig.setGlobalDefaultFromBuilder(builder);
        try {
            DateTime dt = DateTime.parse("31/50/2013 00:30:05");
            fail("Expected IllegalArgumentException in DMY order, but got " + dt.toString());
        } catch (IllegalArgumentException ex) {
            // expected
        }

    }

    /**
     * In Kolkata (formerly Calcutta) India prior to 1945, the timezone offset
     * from UTC was +5:53:20.  Current IST (as of 2014) is UTC+5:30.  So, the number of
     * milliseconds from midnight on a pre-1945 date to midnight on a post-1945 date
     * will not be a multiple of a strict 24 hr day, even with DST not being a factor.
     * It requires an adjustment of 23m20s between the two dates.  Note that even this
     * involves a concession-- there were multiple time zones between Calcutta, Bombay,
     * and Madras.
     * <p/>
     * Note, these changes are provided by the TimeZone object-- and are relevant
     * to countries in many other locales.  India is chosen as a convenient example.
     */
    public void testHistoricalOffsetChange() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        Date d = df.parse("1924-04-15");
        long time0 = d.getTime();
        long time1 = Tm.calcTime(1924, 4, 15, 0, 0, 0, 0, tz);
        long time2 = new DateTime("1924-04-15", tz).toMillis();
        DateTime d2 = new DateTime(time1, tz);
        long time3 = d2.toMillis();
        long time4 = new DateTime("2004-04-15", tz).toMillis();
        assertEquals(time0, time1);
        assertEquals(time0, time2);
        assertEquals(time0, time3);

        long minute = 60000;
        long second = 1000;
        long day = 86400000;
        // 23 minutes and 20 seconds (and a bunch of days) apart
        assertEquals(23 * minute + 20 * second, (time4 - time3) % day);
    }

}
