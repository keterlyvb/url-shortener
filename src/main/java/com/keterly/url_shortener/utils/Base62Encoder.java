package com.keterly.url_shortener.utils;

public class Base62Encoder {

    private static final char[] BASE62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String encode(long value) {
        if (value == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();

        while (value > 0) {
            int remainder = (int) (value % 62);
            result.append(BASE62[remainder]);
            value = value / 62;
        }

        return result.reverse().toString();
    }

    public static long decode(String value) {
        long result = 0;

        for (char c : value.toCharArray()) {
            result = result * 62 + indexOf(c);
        }

        return result;
    }

    private static int indexOf(char c) {
        for (int i = 0; i < BASE62.length; i++) {
            if (BASE62[i] == c) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid Base62 character: " + c);
    }
}
