package com.openapistore.utils;

public final class LongString {
    private LongString() {
    }

    public static String repeat(char ch, int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
