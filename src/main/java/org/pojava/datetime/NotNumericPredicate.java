package org.pojava.datetime;

final class NotNumericPredicate implements CharPredicate {

    static final NotNumericPredicate INSTANCE = new NotNumericPredicate();

    private NotNumericPredicate() {
    }

    public boolean test(char c) {
        return isNotDigit(c);
    }

    static boolean isNotDigit(char c) {
        return c < '0' || c > '9';
    }
}
