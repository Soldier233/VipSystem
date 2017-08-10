package com.anitech.versioncomprator;

/**
 * @author Tapas
 *
 */
public class VersionTokenizer {
	
    private final String versionString;
    private final int length;
    private int position;
    private int number;
    private String suffix;

    public int getNumber() {
        return number;
    }

    public String getSuffix() {
        return suffix;
    }

    public VersionTokenizer(String argVersionString) {
        if (argVersionString == null){
            throw new IllegalArgumentException("versionString parameter canot be null!");
        }
        
        versionString = argVersionString;
        length = argVersionString.length();
    }

    public boolean MoveNext() {
        number = 0;
        suffix = "";

        // No more characters
        if (position >= length){
            return false;
        }

        while (position < length) {
            char c = versionString.charAt(position);
            if (c < '0' || c > '9') break;
            number = number * 10 + (c - '0');
            position++;
        }

        int suffixStart = position;
        while (position < length) {
            char c = versionString.charAt(position);
            if (c == '.') break;
            position++;
        }

        suffix = versionString.substring(suffixStart, position);
        if (position < length) position++;

        return true;
    }
}