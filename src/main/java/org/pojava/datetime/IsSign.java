package org.pojava.datetime;

final class IsSign implements CharPredicate {

    static final IsSign INSTANCE = new IsSign();

    private IsSign() {
    }

    public boolean test(char c) {
        return c == '+' || c == '-';
    }
}
