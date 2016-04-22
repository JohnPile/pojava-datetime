package org.pojava.datetime;

final class CharPattern {

    private final CharPredicate[] predicates;

    CharPattern(CharPredicate... predicates) {
        this.predicates = predicates;
    }

    boolean matches(CharSequence cs, int start) {
        if (start + predicates.length > cs.length()) return false;
        for(int i = 0; i < predicates.length; i++) {
            if (!predicates[i].test(cs.charAt(start + i))) return false;
        }
        return true;
    }

}
