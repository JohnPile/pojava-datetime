package org.pojava.datetime;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MutableStringTest {

    static final String sampleString = "  abCdef123 ";
    @Test
    public void toUpperCase() throws Exception {
        MutableString ms = new MutableString(sampleString);
        ms.upperCase();
        assertEquals(sampleString.toUpperCase(), ms.toString());

        MutableString ms2 = new MutableString(sampleString);
        ms2.subSequence(3, 4).upperCase();

        assertEquals("  aBCdef123 ", ms2.toString());
    }

    @Test
    public void setChar() {
        MutableString ms = new MutableString(sampleString);
        ms.setChar(3, 'z');
        assertEquals('z', ms.charAt(3));

        MutableString subSequence = ms.subSequence(5);
        subSequence.setChar(3, 'z');
        assertEquals('z', subSequence.charAt(3));

    }

    @Test
    public void trim() throws Exception {
        MutableString ms = new MutableString(sampleString);
        ms.trim();
        assertEquals("abCdef123", ms.toString());
    }

    @Test
    public void isDigit() throws Exception {
        MutableString ms = new MutableString(sampleString);

        for(int i = 0; i < sampleString.length(); i++) {
            char sc = sampleString.charAt(i);
            char msc = ms.charAt(i);
            assertEquals(sc, msc);
            assertEquals(Character.digit(sc, 10) >= 0, ms.isDigit(i));
        }
    }

    @Test
    public void onlyDigits() throws Exception {
        MutableString ms = new MutableString(sampleString);
        assertFalse(ms.onlyDigits());
        assertTrue(ms.subSequence(8, 11).onlyDigits());
    }

    @Test
    public void parseInt() throws Exception {
        MutableString ms = new MutableString(sampleString);
        assertEquals(123, ms.parseInt(8, 11));
        assertEquals(123, ms.subSequence(8, 11).parseInt());


        assertEquals(12, new MutableString("12ab").parseInt());
        assertEquals(0, new MutableString("q12").parseInt());
    }


    @Test
    public void isInteger() throws Exception {
        MutableString ms = new MutableString(sampleString);
        assertFalse(ms.isInteger());
        assertTrue(ms.isInteger(8, 11));
        assertTrue(ms.subSequence(8, 11).isInteger());
    }

    @Test
    public void isAlpha() throws Exception {
        MutableString ms = new MutableString(sampleString);
        assertFalse(ms.isAlpha(0));
        assertTrue(ms.isAlpha(3));
        assertFalse(ms.isAlpha(8));
    }

    @Test
    public void add() throws Exception {
        MutableString ms = new MutableString(sampleString);
        ms.add('a', 3);
        assertEquals("  aabCdef123 ", ms.toString());
    }

    @Test
    public void indexOf() throws Exception {
        MutableString ms = new MutableString(sampleString);
        assertEquals(4,  ms.indexOf('C'));
        assertEquals(-1,  ms.indexOf('Z'));
    }


    @Test
    public void endsWith() throws Exception {
        MutableString ms = new MutableString(sampleString);
        assertTrue(ms.endsWith("123 "));
        assertFalse(ms.endsWith(sampleString + "ssss"));
        assertFalse(ms.endsWith("1234"));
    }

    @Test
    public void split() throws Exception {
        MutableString ms = new MutableString(sampleString);
        final List<MutableString> split = ms.split(new CharPredicate() {
            @Override
            public boolean test(char c) {
                return Character.isLetter(c);
            }
        });

        assertEquals(2, split.size());
        assertEquals("  ", split.get(0).toString());
        assertEquals("123 ", split.get(1).toString());
    }

    @Test
    public void deleteWithArrayIndex() throws Exception {
        MutableString ms = new MutableString(sampleString);
        MutableString ms2 = ms.subSequence(3, 7);
        ms2.deleteWithArrayIndex(4, 5);
        assertEquals("  abdef123  ", ms.toString());
        assertEquals("bde", ms2.toString());
    }

}