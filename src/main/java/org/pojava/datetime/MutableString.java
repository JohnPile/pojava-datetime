package org.pojava.datetime;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MutableString implements CharSequence {

    private int startIndex;
    private int endIndex;
    private char[] charArray;


    public MutableString(String str) {
        charArray = str.toCharArray();
        endIndex = str.length();
    }

    private MutableString(char[] charArray, int startIndex, int endIndex) {
        this.charArray = charArray;
        this.endIndex = endIndex;
        this.startIndex = startIndex;
    }

    public MutableString toUpperCase() {
        for(int i = startIndex; i < endIndex; i++) {
            charArray[i] = Character.toUpperCase(charArray[i]);
        }
        return this;
    }

    public MutableString trim() {
        startIndex = firstNonWhiteSpace();
        endIndex = lastNonWhiteSpacePlusOne();
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

    public int length() {
        return endIndex - startIndex;
    }

    public MutableString subSequence(int start, int end) {
        return new MutableString(charArray, startIndex + start, startIndex + end);
    }

    public char charAt(int i) {
        return charArray[i + startIndex];
    }

    public boolean isDigit(int i) {
        final char c = charAt(i);
        return c >= '0' && c <= '9';
    }

    public void setChar(int i, char c) {
        charArray[i] = c;
    }

    public String toString() {
        return new String(charArray, startIndex, endIndex - startIndex);
    }


    public boolean onlyDigits(int start, int end) {
        for(int i = start; i < end; i++) {
            char c = charAt(i);
            if (isNotDigit(c)) return false;
        }
        return true;
    }

    private boolean isNotDigit(char c) {
        if (c < '0' || c > '9') {
            return true;
        }
        return false;
    }

    public int parseInt(int start, int end) {
        if (charAt(start) == '+') {
            start ++;
        }

        int v = 0;
        int m = 1;
        for(int i = start; i < end; i++) {
            char c = charAt(i);
            if (i == 0 && c == '-') {
                m = -1;
            } else {
                v = (v * 10) + (c - '0');
            }
        }
        return v * m;
    }

    public boolean onlyDigits() {
        return onlyDigits(0, length());
    }

    public int parseInt() {
        return parseInt(0, length());
    }

    public boolean isInteger() {
        return isInteger(0, length());
    }

    public boolean isInteger(int start, int end) {
        for(int i = start; i < end; i++) {
            char c = charAt(i);
            if (isNotDigit(c) && !((c == '+' || c == '-')  &&  i == 0))
                return false;
        }
        return true;
    }

    public boolean isAlpha(int i) {
        char c = charAt(i);
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public void add(char c, int i) {
        i += startIndex;
        char[] newChars = new char[charArray.length + 1];
        System.arraycopy(charArray, 0, newChars, 0, i);
        System.arraycopy(charArray, i, newChars, i + 1, charArray.length - i);
        newChars[i] = c;
        charArray = newChars;
        endIndex ++;
    }

    public int indexOf(char c) {
        for(int i = 0; i < length(); i++) {
            if (charAt(i) == c) return i;
        }
        return -1;
    }

    public MutableString subSequence(int start) {
        return subSequence(start, endIndex - startIndex);
    }

    public boolean endsWith(String v) {
        if (v.length() > length()) return false;
        int startIndex = length() - v.length();
        for(int i = v.length() - 1; i >= 0 ; i--) {
            if (v.charAt(i) != charAt(startIndex + i)) {
                return false;
            }
        }
        return true;
    }

    public List<MutableString> split(CharPredicate predicate) {
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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            if (o instanceof CharSequence) {
                return charSequenceEquals((CharSequence)o);
            }
        }

        MutableString mutableString1 = (MutableString) o;

        if (startIndex != mutableString1.startIndex) return false;
        if (endIndex != mutableString1.endIndex) return false;
        return Arrays.equals(charArray, mutableString1.charArray);

    }

    private boolean charSequenceEquals(CharSequence charSequence) {
        if (charSequence.length() != length()) return false;
        for(int i = 0; i < length(); i++) {
            if (charSequence.charAt(i) != charAt(i)) return false;
         }
        return true;
    }

    @Override
    public int hashCode() {
        int result = startIndex;
        result = 31 * result + endIndex;
        result = 31 * result + Arrays.hashCode(charArray);
        return result;
    }

    public void deleteWithArrayIndex(int startIndex, int endIndex) {
        if (endIndex == this.endIndex) {
            setEndIndex(startIndex);
        } else {
            System.arraycopy(charArray, endIndex, charArray, startIndex, charArray.length - endIndex);
            setEndIndex(this.endIndex - endIndex + startIndex);
        }
    }
}
