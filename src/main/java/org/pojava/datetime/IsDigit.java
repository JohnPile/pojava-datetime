package org.pojava.datetime;

/**
 *
 */
final class IsDigit implements CharPredicate {

    static final IsDigit INSTANCE = new IsDigit();

    private IsDigit() {
    }
    public boolean test(char c) {
        return Character.isDigit(c);
    }
}
