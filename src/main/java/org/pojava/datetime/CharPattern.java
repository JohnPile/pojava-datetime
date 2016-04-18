package org.pojava.datetime;

public final class CharPattern {

    private final CharPredicate[] predicates;

    public CharPattern(CharPredicate... predicates) {
        this.predicates = predicates;
    }

    public boolean matches(CharSequence cs, int start) {
        if (start + predicates.length > cs.length()) return false;
        for(int i = 0; i < predicates.length; i++) {
            if (!predicates[i].test(cs.charAt(start + i))) return false;
        }
        return true;
    }

}
