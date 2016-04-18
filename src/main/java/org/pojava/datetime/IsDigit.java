package org.pojava.datetime;

public final class IsDigit implements CharPredicate {

    public static final IsDigit INSTANCE = new IsDigit();

    private IsDigit() {
    }
    public boolean test(char c) {
        return Character.isDigit(c);
    }
}
