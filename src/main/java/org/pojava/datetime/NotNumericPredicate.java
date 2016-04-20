package org.pojava.datetime;

public final class NotNumericPredicate implements CharPredicate {

    public static final NotNumericPredicate INSTANCE = new NotNumericPredicate();

    private NotNumericPredicate() {
    }

    public boolean test(char c) {
        return isNotDigit(c);
    }

    public static boolean isNotDigit(char c) {
        return c < '0' || c > '9';
    }
}
