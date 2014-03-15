package org.pojava.datetime;

import junit.framework.TestCase;

public class ShiftTester extends TestCase {

    public void testToString24hr() {
        Shift shift = new Shift();
        shift.setDay(5);
        shift.setHour(24);
        shift.setMinute(5);
        // Expect 24h to be preserved
        assertEquals("P5DT24H5M", shift.toString());
    }

    public void testLargeValues() {
        Shift shift = new Shift();
        shift.setYear(54);
        shift.setMonth(3);
        shift.setWeek(2);
        assertEquals("P54Y3M2W", shift.toString());
    }

    public void testDayWeekPreservation() {
        Shift shift = new Shift();
        shift.setWeek(3);
        shift.setDay(21);
        assertEquals("P3W21D", shift.toString());
    }

    public void testToStringOverflows() {
        Shift shift = new Shift();
        shift.setHour(24);
        shift.setMinute(60);
        shift.setSecond(60);
        shift.setNanosec(1000000000);
        assertEquals("PT25H1M1S", shift.toString());
    }

    public void testToStringNanos() {
        Shift shift = new Shift();
        shift.setNanosec(1234567);
        assertEquals("PT0.001234567S", shift.toString());
    }

    public void testToStringSecNanos() {
        Shift shift = new Shift();
        shift.setSecond(12);
        shift.setNanosec(1234567);
        assertEquals("PT12.001234567S", shift.toString());
        long longNano = 0;
        shift.setNanosec(longNano);
        assertEquals("PT12S", shift.toString());
    }

    public void testConstructor() {
        String[] samples = {"PT-1.2345S", "P1M2DT3M4S", "PT12.001234567S", "P1DT24H13S", "P130Y7M6W2DT5H6M7.3S",
                "PT1.1S", "PT1.999999999S"};
        for (String sample : samples) {
            assertEquals(sample, new Shift(sample).toString());
        }
    }

    public void testFracYears() {
        Shift shift = new Shift("P1Y");
        shift.shiftYears(3.5);
        assertEquals("P4Y6M", shift.toString());
        shift.shiftYears(-1.25);
        assertEquals("P3Y3M", shift.toString());
    }

    public void testFracWeeks() {
        Shift shift = new Shift("P1W");
        shift.shiftWeeks(3.5);
        assertEquals("P4W3DT12H", shift.toString());
        shift.shiftWeeks(-1.25);
        assertEquals("P3W2DT-6H", shift.toString());
    }

    public void testSettleDay() {
        Shift shift = new Shift("P1W-1D");
        assertEquals("P6D", shift.toString());
    }

    /**
     * Unsettled because day may be 23, 24, or 25 hr.
     */
    public void testSettleHour() {
        Shift shift = new Shift("P1DT-1H");
        assertEquals("P1DT-1H", shift.toString());
    }

    public void testSettleMinute() {
        Shift shift = new Shift("PT1H-1M");
        assertEquals("PT59M", shift.toString());
    }

    public void testSettleSecond() {
        Shift shift = new Shift("PT1M-1S");
        assertEquals("PT59S", shift.toString());
    }

}
