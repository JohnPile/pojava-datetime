package org.pojava.datetime;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MutableString implements CharSequence {

    private int startIndex;
    private int endIndex;
    private char[] charArray;


    MutableString(String str) {
        charArray = str.toCharArray();
        endIndex = str.length();
    }

    private MutableString(char[] charArray, int startIndex, int endIndex) {
        this.charArray = charArray;
        this.endIndex = endIndex;
        this.startIndex = startIndex;
    }

    MutableString upperCase() {
        for(int i = startIndex; i < endIndex; i++) {
            charArray[i] = Character.toUpperCase(charArray[i]);
        }
        return this;
    }

    MutableString trim() {
        endIndex = lastNonWhiteSpacePlusOne();
        startIndex = firstNonWhiteSpace();
        return this;
    }

    private int lastNonWhiteSpacePlusOne() {
        for(int i = length() - 1; i >= 0; i--) {
            if (charAt(i) != ' ') return i + 1;
        }
        return startIndex;
    }

    private int firstNonWhiteSpace() {
        for(int i = 0; i < length(); i++) {
            if (charAt(i) != ' ') return i;
        }
        return endIndex;
    }

    @Override
    public int length() {
        return endIndex - startIndex;
    }

    @Override
    public MutableString subSequence(int start, int end) {
        return new MutableString(charArray, calculateIndex(start), calculateIndex(end));
    }

    @Override
    public char charAt(int i) {
        return charArray[calculateIndex(i)];
    }

    private int calculateIndex(int i) {
        return i + startIndex;
    }

    boolean isDigit(int i) {
        final char c = charAt(i);
        return c >= '0' && c <= '9';
    }

    void setChar(int i, char c) {
        charArray[calculateIndex(i)] = c;
    }

    @Override
    public String toString() {
        return new String(charArray, startIndex, endIndex - startIndex);
    }


    boolean onlyDigits(int start, int end) {
        for(int i = start; i < end; i++) {
            char c = charAt(i);
            if (isNotDigit(c)) return false;
        }
        return true;
    }

    private boolean isNotDigit(char c) {
        return c < '0' || c > '9';
    }

    int parseInt(int start, int end) {
        if (charAt(start) == '+') {
            start ++;
        }

        int v = 0;
        int m = 1;
        for(int i = start; i < end; i++) {
            char c = charAt(i);
            if (i == 0 && c == '-') {
                m = -1;
            } else if (c >= '0' && c <= '9'){
                v = (v * 10) + (c - '0');
            } else {
                break;
            }
        }
        return v * m;
    }

    boolean onlyDigits() {
        return onlyDigits(0, length());
    }

    int parseInt() {
        return parseInt(0, length());
    }

    boolean isInteger() {
        return isInteger(0, length());
    }

    boolean isInteger(int start, int end) {
        for(int i = start; i < end; i++) {
            char c = charAt(i);
            if (isNotDigit(c) && !((c == '+' || c == '-')  &&  i == 0))
                return false;
        }
        return true;
    }

    boolean isAlpha(int i) {
        char c = charAt(i);
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    void add(char c, int i) {
        i += startIndex;
        char[] newChars = new char[charArray.length + 1];
        System.arraycopy(charArray, 0, newChars, 0, i);
        System.arraycopy(charArray, i, newChars, i + 1, charArray.length - i);
        newChars[i] = c;
        charArray = newChars;
        endIndex ++;
    }

    int indexOf(char c) {
        for(int i = 0; i < length(); i++) {
            if (charAt(i) == c) return i;
        }
        return -1;
    }

    MutableString subSequence(int start) {
        return subSequence(start, endIndex - startIndex);
    }

    boolean endsWith(String v) {
        if (v.length() > length()) return false;
        int startIndex = length() - v.length();
        for(int i = v.length() - 1; i >= 0 ; i--) {
            if (v.charAt(i) != charAt(startIndex + i)) {
                return false;
            }
        }
        return true;
    }

    List<MutableString> split(CharPredicate predicate) {
        List<MutableString> list = new ArrayList<MutableString>(10);

        int startIndex = 0;
        boolean hasContent = false;
        for(int i = 0; i < length(); i++) {
            char c = charAt(i);
            if (predicate.test(c)) {
                if (hasContent) {
                    list.add(subSequence(startIndex, i));
                }
                startIndex = i + 1;
                hasContent = false;
            } else {
                hasContent = true;
            }
        }

        if (hasContent) {
            list.add(subSequence(startIndex, length()));
        }

        return list;
    }

    int getStartIndex() {
        return startIndex;
    }

    int getEndIndex() {
        return endIndex;
    }

    boolean matches(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        if (cs.length() != length()) {
            return false;
        }
        for(int i = 0; i < length(); i++) {
            if (charAt(i) != cs.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean charSequenceEquals(CharSequence charSequence) {
        if (charSequence.length() != length()) return false;
        for(int i = 0; i < length(); i++) {
            if (charSequence.charAt(i) != charAt(i)) return false;
         }
        return true;
    }


    void deleteWithArrayIndex(int startIndex, int endIndex) {
        if (endIndex == this.endIndex) {
            this.endIndex = startIndex;
        } else {
            System.arraycopy(charArray, endIndex, charArray, startIndex, charArray.length - endIndex);
            this.endIndex = (this.endIndex - endIndex + startIndex);
        }
    }
}
