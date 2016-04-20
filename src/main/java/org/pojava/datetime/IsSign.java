package org.pojava.datetime;

public final class IsSign implements CharPredicate {

    public static final IsSign INSTANCE = new IsSign();

    private IsSign() {
    }

    public boolean test(char c) {
        return c == '+' || c == '-';
    }
}
