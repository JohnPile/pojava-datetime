package org.pojava.datetime;

public final class IsChar implements  CharPredicate {

    public static IsChar of(char c) {
        return new IsChar(c);
    }

    private final char value;

    private IsChar(char value) {
        this.value = value;
    }

    public boolean test(char c) {
        return this.value == c;
    }
}
