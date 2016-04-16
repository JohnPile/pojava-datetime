package org.pojava.datetime;

import junit.framework.TestCase;

public class MonthMapTester extends TestCase {

    private MonthMap monthMap;

    @Override
    public void setUp() {
        monthMap = MonthMap.fromAllLocales();
    }

    public void testFullMonthName_EN() throws Exception {
        assertEquals(0, monthMap.monthIndex("January").intValue());
        assertEquals(1, monthMap.monthIndex("February").intValue());
        assertEquals(2, monthMap.monthIndex("March").intValue());
        assertEquals(3, monthMap.monthIndex("April").intValue());
        assertEquals(4, monthMap.monthIndex("May").intValue());
        assertEquals(5, monthMap.monthIndex("June").intValue());
        assertEquals(6, monthMap.monthIndex("July").intValue());
        assertEquals(7, monthMap.monthIndex("August").intValue());
        assertEquals(8, monthMap.monthIndex("September").intValue());
        assertEquals(9, monthMap.monthIndex("October").intValue());
        assertEquals(10, monthMap.monthIndex("November").intValue());
        assertEquals(11, monthMap.monthIndex("December").intValue());
    }

    public void testFullMonthName_FR() throws Exception {
        assertEquals(0, monthMap.monthIndex("janvier").intValue());
        assertEquals(1, monthMap.monthIndex("février").intValue());
        assertEquals(2, monthMap.monthIndex("mars").intValue());
        assertEquals(3, monthMap.monthIndex("avril").intValue());
        assertEquals(4, monthMap.monthIndex("mai").intValue());
        assertEquals(5, monthMap.monthIndex("juin").intValue());
        assertEquals(6, monthMap.monthIndex("juillet").intValue());
        assertEquals(7, monthMap.monthIndex("août").intValue());
        assertEquals(8, monthMap.monthIndex("septembre").intValue());
        assertEquals(9, monthMap.monthIndex("octobre").intValue());
        assertEquals(10, monthMap.monthIndex("novembre").intValue());
        assertEquals(11, monthMap.monthIndex("décembre").intValue());
    }

    public void testAbbreviation_EN() throws Exception {
        assertEquals(0, monthMap.monthIndex("jan").intValue());
        assertEquals(1, monthMap.monthIndex("FEB").intValue());
        assertEquals(2, monthMap.monthIndex("Mar").intValue());
        assertEquals(3, monthMap.monthIndex("apr").intValue());
        assertEquals(4, monthMap.monthIndex("MAY").intValue());
        assertEquals(5, monthMap.monthIndex("Jun").intValue());
        assertEquals(6, monthMap.monthIndex("jul").intValue());
        assertEquals(7, monthMap.monthIndex("AUG").intValue());
        assertEquals(8, monthMap.monthIndex("Sep").intValue());
        assertEquals(9, monthMap.monthIndex("oct").intValue());
        assertEquals(10, monthMap.monthIndex("NOV").intValue());
        assertEquals(11, monthMap.monthIndex("Dec").intValue());
    }

    public void testThreeCharacterAbbreviationWithPeriod() throws Exception {
        assertEquals(0, monthMap.monthIndex("jan.").intValue());
        assertEquals(1, monthMap.monthIndex("FEB.").intValue());
        assertEquals(2, monthMap.monthIndex("Mar.").intValue());
        assertEquals(3, monthMap.monthIndex("apr.").intValue());
        assertEquals(4, monthMap.monthIndex("MAY.").intValue());
        assertEquals(5, monthMap.monthIndex("Jun.").intValue());
        assertEquals(6, monthMap.monthIndex("jul.").intValue());
        assertEquals(7, monthMap.monthIndex("AUG.").intValue());
        assertEquals(8, monthMap.monthIndex("Sep.").intValue());
        assertEquals(9, monthMap.monthIndex("oct.").intValue());
        assertEquals(10, monthMap.monthIndex("NOV.").intValue());
        assertEquals(11, monthMap.monthIndex("Dec.").intValue());
    }

    public void testThreeCharacterAbbreviation_IT() throws Exception {
        assertEquals(0, monthMap.monthIndex("genn.").intValue());
        assertEquals(1, monthMap.monthIndex("febbr.").intValue());
        assertEquals(2, monthMap.monthIndex("mar.").intValue());
        assertEquals(3, monthMap.monthIndex("apr.").intValue());
        assertEquals(4, monthMap.monthIndex("magg.").intValue());
        assertEquals(5, monthMap.monthIndex("giugno").intValue());
        assertEquals(6, monthMap.monthIndex("luglio").intValue());
        assertEquals(7, monthMap.monthIndex("ago.").intValue());
        assertEquals(8, monthMap.monthIndex("sett.").intValue());
        assertEquals(9, monthMap.monthIndex("ott.").intValue());
        assertEquals(10, monthMap.monthIndex("nov.").intValue());
        assertEquals(11, monthMap.monthIndex("dic.").intValue());
    }
}
