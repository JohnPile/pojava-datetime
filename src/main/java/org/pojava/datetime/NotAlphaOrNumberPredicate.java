package org.pojava.datetime;

public final class NotAlphaOrNumberPredicate implements CharPredicate {

    public static final NotAlphaOrNumberPredicate INSTANCE = new NotAlphaOrNumberPredicate();

    private NotAlphaOrNumberPredicate() {
    }

    public boolean test(char c) {
        return !Character.isLetter(c) && NotNumericPredicate.isNotDigit(c);
    }
}
