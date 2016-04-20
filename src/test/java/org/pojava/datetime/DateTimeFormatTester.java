package org.pojava.datetime;

import junit.framework.TestCase;
import org.pojava.datetime.examples.EuroDateTimeConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Unit tests to verify formatter output against SimpleDateFormat output
 * <p/>
 * http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
 *
 * @author John Pile
 */
public class DateTimeFormatTester extends TestCase {

    private DateTimeConfigBuilder configBuilder() {
        DateTimeConfigBuilder dtcBuilder = DateTimeConfigBuilder.newInstance();
        TimeZone tz = TimeZone.getDefault();
        dtcBuilder.setMonthMap(MonthMap.fromAllLocales());
        dtcBuilder.getTzMap().put("Z", "UTC");
        dtcBuilder.getTzCache().put(tz.getID(), tz);
        return dtcBuilder;
    }

    @Override
    public void setUp() {
        DateTimeConfig.setGlobalDefaultFromBuilder(configBuilder());
    }

    public void testCommonFormats() {
        DateTime dt = new DateTime("1/23/45 6:7:8.9101112");
        compareStatic("yy/M/d", dt);
        compareStatic("yyyyy/MMMMM/ddddd", dt);
        compareStatic("MM/dd/yyyy h:m:s.SSS", dt);
        compareStatic("h 'o''clock' a", dt);
        compareStatic("'o''xx' ''''", dt);
    }

    public void testUniqueFormats() {
        DateTime dt = new DateTime("1/23/2045 6:7:8.9101112");
        String fmt = "yyyy-MM-dd hh:mm:ss.SSSSSSSSS";
        assertEquals("2045-01-23 06:07:08.910111200", DateTimeFormat.format(fmt, dt));
    }

    public void testHTimeFormat() {
        IDateTimeConfig config = new EuroDateTimeConfig();
        DateTime dt = new DateTime("21.09.2012 - 21h48", config);
        assertEquals("2012-09-21 21:48:00", dt.toString());
    }

    public void testFormat_G_BC() {
        DateTime dt = new DateTime("1/23/2045 BC");
        String expect[] = {"BC", "BC", "BC", "BC"};
        compareFormat(dt, 'G', expect);
        compareFormat(dt, 'G', dt.config().getLocale());
    }

    public void testFormat_G_AD() {
        DateTime dt = new DateTime("1/23/2045");
        String expect[] = {"AD", "AD", "AD", "AD"};
        compareFormat(dt, 'G', expect);
        compareFormat(dt, 'G', dt.config().getLocale());
    }

    public void testFormat_g_BC() {
        DateTime dt = new DateTime("1/23/2045 BC");
        String expect[] = {"BCE", "BCE", "BCE", "BCE"};
        compareFormat(dt, 'g', expect);
    }

    public void testFormat_g_AD() {
        DateTime dt = new DateTime("1/23/2045");
        String expect[] = {"CE", "CE", "CE", "CE"};
        compareFormat(dt, 'g', expect);
    }

    public void testFormat_y_BC() {
        DateTime dt = new DateTime("1/23/2045 BC");
        String expect[] = {"-45", "-45", "-045", "-2045"};
        compareFormat(dt, 'y', expect);
        DateTimeConfigBuilder dtcBuilder = DateTimeConfigBuilder.newInstance();
        dtcBuilder.bcPrefix("");
        DateTimeConfig dtc = DateTimeConfig.fromBuilder(dtcBuilder);
        DateTime dt2 = new DateTime("1/23/2045 BC", dtc);
        assertEquals("2045", dt2.toString("yyyy"));
        // Default behavior shows a bare BC year as negative
        assertEquals("-2045", dt.toString("yyyy"));
        // If 'G' or 'g' is present, bcPrefix is omitted from year.
        assertEquals("2045 BC", dt.toString("yyyy G"));
        assertEquals("2045 BCE", dt.toString("yyyy g"));
    }

    public void testFormat_y_AD() {
        DateTime dt = new DateTime("1/23/2045 6:7:8.9101112");
        String expect[] = {"45", "45", "045", "2045"};
        compareFormat(dt, 'y', expect);
        // SimpleDateFormat truncates "yyy" to two-digit year.
        // DateTime.format truncates "yyy" to three-digit year.
    }

    public void testFormat_M() {
        DateTime dt = new DateTime("1/23/2045 6:7:8.9101112");
        String expect[] = {"1", "01", "Jan", "January"};
        compareFormat(dt, 'M', dt.config().getLocale());
        compareFormat(dt, 'M', expect);
    }

    public void testFormat_M_FR() {
        DateTimeConfigBuilder builder = configBuilder();
        builder.setLocale(Locale.FRENCH);
        DateTimeConfig dtc = DateTimeConfig.fromBuilder(builder);
        DateTime dt = new DateTime("1/23/2045 6:7:8.9101112", dtc);
        String expect[] = {"1", "01", "janv.", "janvier"};
        compareFormat(dt, 'M', Locale.FRENCH);
        compareFormat(dt, 'M', expect);
    }

    public void testFormat_M_DE() {
        DateTimeConfigBuilder builder = configBuilder();
        builder.setLocale(Locale.GERMAN);
        DateTimeConfig dtc = DateTimeConfig.fromBuilder(builder);
        DateTime dt = new DateTime("10/23/2045 6:7:8.9101112", dtc);
        String expect[] = {"10", "10", "Okt", "Oktober"};
        compareFormat(dt, 'M', Locale.GERMAN);
        compareFormat(dt, 'M', expect);
    }

    public void testFormat_M_IT() {
        DateTimeConfigBuilder builder = configBuilder();
        builder.setLocale(Locale.ITALIAN);
        DateTimeConfig dtc = DateTimeConfig.fromBuilder(builder);
        DateTime dt = new DateTime("1/23/2045 6:7:8.9101112", dtc);
        String expect[] = {"1", "01", "gen", "gennaio"};
        compareFormatIgnoreCase(dt, 'M', Locale.ITALIAN);
        compareFormat(dt, 'M', expect);
    }

    /**
     * Differs from SimpleDateTime. This version uses RFC-8601 definition of week-in-month
     */
    public void testFormat_W() { // Week in month
        // Dec 1 2014 starts on a Monday
        DateTime dt = new DateTime("12/13/2014 15:16:17:18.192021");
        String expect[] = {"2", "02", "002", "0002"};
        compareFormat(dt, 'W', dt.config().getLocale());
        compareFormat(dt, 'W', expect);
    }

    public void testFormat_W_ranges() { // Week in month
        // ISO 8601 bases month placement of Mon-Sun week by where Thu lies.
        assertEquals("1", new DateTime("01/01/1998").toString("W"));
        assertEquals("5", new DateTime("12/31/1998").toString("W"));
        assertEquals("5", new DateTime("01/01/1999").toString("W"));
        assertEquals("5", new DateTime("12/31/1999").toString("W"));
        assertEquals("5", new DateTime("01/01/2000").toString("W"));
        assertEquals("4", new DateTime("12/31/2000").toString("W"));
    }

    public void testFormat_w() { // Week in year
        DateTime dt = new DateTime("12/13/2014 15:16:17:18.192021");
        String expect[] = {"50", "50", "050", "0050"};
        compareFormat(dt, 'w', dt.config().getLocale());
        compareFormat(dt, 'w', expect);
    }

    public void testFormat_w_ranges() { // Week in year
        // ISO 8601 bases month placement of Mon-Sun week by where Thu lies.
        assertEquals("1", new DateTime("01/01/1998").toString("w"));
        assertEquals("53", new DateTime("12/31/1998").toString("w"));
        assertEquals("53", new DateTime("01/01/1999").toString("w"));
        assertEquals("52", new DateTime("12/31/1999").toString("w"));
        assertEquals("52", new DateTime("01/01/2000").toString("w"));
        assertEquals("52", new DateTime("12/31/2000").toString("w"));
    }

    public void testFormat_D() { // Day of the year
        DateTime dt = new DateTime("2/23/2045 6:7:8.9101112");
        String expect[] = {"54", "54", "054", "0054"};
        compareFormat(dt, 'D', dt.config().getLocale());
        compareFormat(dt, 'D', expect);
    }

    public void testFormat_d() { // Day of month
        DateTime dt = new DateTime("4/4/2045 6:7:8.9101112");
        String expect[] = {"4", "04", "004", "0004"};
        compareFormat(dt, 'd', dt.config().getLocale());
        compareFormat(dt, 'd', expect);
    }

    public void testFormat_E() { // Day of week
        // 12/31/1998 is a Wednesday
        DateTime dt = new DateTime("12/31/1997 15:16:17.192021");
        String expect[] = {"Wed", "Wed", "Wed", "Wednesday"};
        compareFormat(dt, 'E', dt.config().getLocale());
        compareFormat(dt, 'E', expect);
    }

    public void testFormat_F() { // Numeric day of week in month
        // Dec 1 2014 starts on a Monday
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"2", "02", "002", "0002"};
        compareFormat(dt, 'F', dt.config().getLocale());
        compareFormat(dt, 'F', expect);
    }

    public void testFormat_a_AM() { // AM / PM
        // Dec 1 2014 starts on a Monday
        DateTime dt = new DateTime("12/13/2014 00:16:17.192021");
        String expect[] = {"AM", "AM", "AM", "AM"};
        compareFormat24(dt, 'a', dt.config().getLocale());
        compareFormat(dt, 'a', expect);
    }

    public void testFormat_a_PM() { // AM / PM
        // Dec 1 2014 starts on a Monday
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"PM", "PM", "PM", "PM"};
        compareFormat24(dt, 'a', dt.config().getLocale());
        compareFormat(dt, 'a', expect);
    }

    public void testFormat_H() { // 24 hour
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"15", "15", "015", "0015"};
        compareFormat24(dt, 'H', dt.config().getLocale());
        compareFormat(dt, 'H', expect);
    }

    public void testFormat_h() { // 12 hour
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"3", "03", "003", "0003"};
        compareFormat24(dt, 'h', dt.config().getLocale());
        compareFormat(dt, 'h', expect);
    }

    public void testFormat_k() { // 24 hour 1-24
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"15", "15", "015", "0015"};
        compareFormat24(dt, 'k', dt.config().getLocale());
        compareFormat(dt, 'k', expect);
    }

    public void testFormat_K() { // 24 hour 1-24
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"3", "03", "003", "0003"};
        compareFormat24(dt, 'K', dt.config().getLocale());
        compareFormat(dt, 'K', expect);
    }

    public void testFormat_m() { // minute
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"16", "16", "016", "0016"};
        compareFormat(dt, 'm', dt.config().getLocale());
        compareFormat(dt, 'm', expect);
    }

    public void testFormat_s() { // second
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"17", "17", "017", "0017"};
        compareFormat(dt, 's', dt.config().getLocale());
        compareFormat(dt, 's', expect);
    }

    public void testFormat_z() { // time zone descriptive
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021 PST");
        SimpleDateFormat sdf = new SimpleDateFormat("z zz zzz zzzz");
        String expect[] = sdf.format(dt.toDate()).split(" ", 4);
        compareFormat(dt, 'z', dt.config().getLocale());
        compareFormat(dt, 'z', expect);
    }

    /**
     * Differs from SimpleDateTime ZZ inserts a colon between hours and minutes
     */
    public void testFormat_Z() { // time zone descriptive
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021 PST");
        SimpleDateFormat sdf = new SimpleDateFormat("Z ZZ ZZZ ZZZZ");
        String times = sdf.format(dt.toDate()).replaceAll("( -\\d\\d)", "$1:");
        String expect[] = times.split(" ", 4);
        expect[1]  = "+00:00";
        expect[2]  = "+00:00";
        expect[3]  = "+00:00";
        compareFormat(dt, 'Z', expect);
    }

    /**
     * Differs from SimpleDateTime S represents fractional seconds, not just milliseconds
     */
    public void testFormat_S() { // sub-second
        DateTime dt = new DateTime("12/13/2014 15:16:17.192021");
        String expect[] = {"1", "19", "192", "1920", "19202", "192021", "1920210"};
        compareFormat(dt, 'S', expect);
    }

    private void compareFormat24(DateTime dt, char formatChar, Locale loc) {
        for (int h = 0; h < 24; h++) {
            DateTime dth = dt.add(CalendarUnit.HOUR, h);
            compareFormat(dth, formatChar, loc);
        }
    }

    /**
     * Verify that formatting matches expected output
     *
     * @param dt         DateTime
     * @param formatChar character to test repeated formats
     * @param expected   output expected for 1 char, 2 chars, ..., n+1 chars
     */
    private void compareFormat(DateTime dt, char formatChar, String[] expected) {
        StringBuilder sb = new StringBuilder();
        for (String element : expected) {
            sb.append(formatChar);
            final String actual = dt.toString(sb.toString());
            assertEquals(element, actual);
        }
    }

    /**
     * Verify that formatting matches output SimpleDateFormat
     *
     * @param dt         DateTime
     * @param formatChar character to test repeated formats
     * @param loc        Locale influencing format
     */
    private void compareFormat(DateTime dt, char formatChar, Locale loc) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(formatChar);
            Locale orig = Locale.getDefault();
            SimpleDateFormat sdf = new SimpleDateFormat(sb.toString(), loc);
            Locale.setDefault(loc);
            try {
                final String actual = dt.toString(sb.toString(), loc);
                final String expected = sdf.format(dt.toDate());
                assertEquals(expected, actual);
            } finally {
                Locale.setDefault(orig);
            }
        }
    }

    private void compareFormatIgnoreCase(DateTime dt, char formatChar, Locale loc) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(formatChar);
            Locale orig = Locale.getDefault();
            SimpleDateFormat sdf = new SimpleDateFormat(sb.toString(), loc);
            Locale.setDefault(loc);
            try {
                final String actual = dt.toString(sb.toString(), loc);
                final String expected = sdf.format(dt.toDate());
                assertTrue(expected.equalsIgnoreCase(actual));
            } finally {
                Locale.setDefault(orig);
            }
        }
    }

    public void testLocale() {
        DateTime dt = new DateTime("2008-01-09 PST");
        compareStatic("zzzz", dt, Locale.FRENCH);
    }


    // Helper methods to verify DateTimeFormat matches SimpleDateFormat
    // ================================================================
    private void compareStatic(String fmt, DateTime dt) {
        Date date = dt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        assertEquals(sdf.format(date), DateTimeFormat.format(fmt, dt));
    }

    private void compareStatic(String fmt, DateTime dt, Locale loc) {
        Date date = dt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt, loc);
        assertEquals(sdf.format(date), DateTimeFormat.format(fmt, dt, TimeZone.getDefault(), loc));
    }

}
