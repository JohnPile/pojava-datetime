package org.pojava.datetime;

final class IsEqual implements  CharPredicate {

    static IsEqual of(char c) {
        return new IsEqual(c);
    }

    private final char value;

    private IsEqual(char value) {
        this.value = value;
    }

    public boolean test(char c) {
        return this.value == c;
    }
}
