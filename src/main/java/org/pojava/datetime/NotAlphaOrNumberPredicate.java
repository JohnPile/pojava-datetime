package org.pojava.datetime;

final class NotAlphaOrNumberPredicate implements CharPredicate {

    static final NotAlphaOrNumberPredicate INSTANCE = new NotAlphaOrNumberPredicate();

    private NotAlphaOrNumberPredicate() {
    }

    public boolean test(char c) {
        return !Character.isLetter(c) && NotNumericPredicate.isNotDigit(c);
    }
}
