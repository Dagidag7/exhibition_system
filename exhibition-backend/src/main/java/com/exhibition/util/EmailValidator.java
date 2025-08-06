package com.exhibition.util;

import java.util.regex.Pattern;

public class EmailValidator {
    
    // RFC 5322 compliant email regex pattern
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * Validates email format using regex pattern
     * @param email The email address to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(email.trim()).matches();
    }
    
    /**
     * Normalizes email by trimming whitespace and converting to lowercase
     * @param email The email address to normalize
     * @return normalized email address
     */
    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
    
    /**
     * Validates email format and returns normalized email
     * @param email The email address to validate and normalize
     * @return normalized email if valid, null if invalid
     */
    public static String validateAndNormalize(String email) {
        if (!isValidFormat(email)) {
            return null;
        }
        return normalize(email);
    }
}