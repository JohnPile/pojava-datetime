package org.pojava.datetime;

import junit.framework.TestCase;

public class CalendarUnitTester extends TestCase {

    public void testParsing() {
        assertEquals(CalendarUnit.WEEK, CalendarUnit.valueOf("WEEK"));
    }

    public void testOrdering() {
        assertTrue(CalendarUnit.WEEK.compareTo(CalendarUnit.MONTH) < 0);
    }

}
