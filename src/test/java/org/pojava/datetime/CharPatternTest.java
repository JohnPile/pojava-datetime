package org.pojava.datetime;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CharPatternTest {
    @Test
    public void matches() throws Exception {

        CharPattern charPattern = new CharPattern(IsEqual.of('c'), IsDigit.INSTANCE);

        assertFalse(charPattern.matches("ddc9ll", 0));
        assertTrue(charPattern.matches("ddc9ll", 2));
        assertFalse(charPattern.matches("c", 0));
        assertFalse(charPattern.matches("cl9", 0));

    }

}