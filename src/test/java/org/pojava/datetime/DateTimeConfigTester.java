package org.pojava.datetime;

import junit.framework.TestCase;

import java.util.TimeZone;

public class DateTimeConfigTester extends TestCase {

    private TimeZone localTz = TimeZone.getDefault();

    private DateTimeConfigBuilder configBuilder() {
        DateTimeConfigBuilder dtcBuilder = DateTimeConfigBuilder.newInstance();
        TimeZone tz = TimeZone.getDefault();
        dtcBuilder.setMonthMap(MonthMap.fromAllLocales());
        dtcBuilder.getTzMap().put("Z", "UTC");
        dtcBuilder.getTzCache().put(tz.getID(), tz);
        dtcBuilder.setDmyOrder(false);
        // UTC + 5:30 Always
        dtcBuilder.setInputTimeZone(TimeZone.getTimeZone("IST"));
        // UTC - 4:00 EST (1st Sun in Nov to 2nd Sun in Mar)
        // UTC - 5:00 EDT (2nd Sun in Mar to 1st Sun in Nov)
        dtcBuilder.setOutputTimeZone(TimeZone.getTimeZone("America/New_York"));
        dtcBuilder.setFormat("yyyy-MM-dd HH:mm:ss z");
        return dtcBuilder;
    }

    protected void setUp() throws Exception {
        super.setUp();
        // Time Zone of the JVM.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        TimeZone.setDefault(localTz);
    }

    /**
     * Test equivalence with UTC.
     */
    public void testUTCeqIST() {
        DateTimeConfig config = DateTimeConfig.fromBuilder(configBuilder());
        DateTime utc = new DateTime("2011-08-31 UTC", config);
        // InputTimeZone defines zone where local time was captured.
        DateTime ist = new DateTime("2011-08-31 05:30:00", config);
        // OutputTimeZone determines zone for which local time is displayed.
        DateTime utc2 = new DateTime("2011-08-31 UTC");
        assertEquals(utc2.getSeconds(), utc.getSeconds());
        assertEquals(utc.getSeconds(), ist.getSeconds());
    }

    /**
     * Test date during Daylight Saving with multiple time zones involved.
     */
    public void testZoneDifference() {
        DateTimeConfig config = DateTimeConfig.fromBuilder(configBuilder());
        // InputTimeZone defines zone where local time was captured.
        DateTime dtc = new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
        assertEquals("2011-08-30 19:01:32 EDT", dtc.toString());
    }

    /**
     * Test override of Time Zone on output.
     */
    public void testZoneOverride() {
        DateTimeConfig config = DateTimeConfig.fromBuilder(configBuilder());
        // InputTimeZone defines zone where local time was captured.
        DateTime dtc = new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
        assertEquals("2011-08-30 16:01:32 PDT", dtc.toString(config.getFormat(), TimeZone.getTimeZone("PST")));
    }

    /**
     * Override the default format
     */
    public void testFormatOverride() {
        DateTimeConfig config = DateTimeConfig.fromBuilder(configBuilder());
        // InputTimeZone defines zone where local time was captured.
        DateTime dtc = new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
        assertEquals("2011-08-30 07:01:32 PM EDT", dtc.toString("yyyy-MM-dd hh:mm:ss a z"));
    }

    /**
     * Override both the time zone and the format.
     */
    public void testZoneAndFormatOverride() {
        DateTimeConfig config = DateTimeConfig.fromBuilder(configBuilder());
        // InputTimeZone defines zone where local time was captured.
        DateTime dtc = new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
        assertEquals("[2011-08-30 4:01:32 PM]", dtc.toString("[yyyy-MM-dd h:mm:ss a]", TimeZone.getTimeZone("PST")));
    }

    public void testTzMap() {
        DateTimeConfigBuilder builder = configBuilder();
        builder.getTzMap().put("CST", "America/Central");
        DateTimeConfig dtc = DateTimeConfig.fromBuilder(builder);
        assertEquals("America/Central", dtc.getTzMap().get("CST"));
    }

    public void testUnspecifiedCenturyAlwaysInPast() {
        DateTimeConfigBuilder builder = configBuilder();
        builder.setUnspecifiedCenturyAlwaysInPast(true);
        DateTimeConfig.setGlobalDefaultFromBuilder(builder);
        int yy = Integer.parseInt(new DateTime().toString("yy"));
        if (yy < 89) {
            yy += 10;
        }
        String longYearDate = "4/15/19" + yy;
        String shortYearDate = "4/15/" + yy;
        assertEquals(new DateTime(longYearDate), new DateTime(shortYearDate));
        builder.setUnspecifiedCenturyAlwaysInPast(false);
        DateTimeConfig.setGlobalDefaultFromBuilder(builder);
        assertFalse(new DateTime(longYearDate).toString().equals(new DateTime(shortYearDate).toString()));
    }

}
