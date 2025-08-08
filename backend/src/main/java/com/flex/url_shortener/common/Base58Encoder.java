package com.flex.url_shortener.common;

public class Base58Encoder {
    private static final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final int BASE58_SIZE = BASE58_ALPHABET.length();

    public static String encodeBase58(long number) {
        if (number == 0) {
            return String.valueOf(BASE58_ALPHABET.charAt(0));
        }

        var encodedResult = new StringBuilder();

        while (number > 0) {
            var remainder = (int) number % BASE58_SIZE;
            number /= BASE58_SIZE;
            encodedResult.append(BASE58_ALPHABET.charAt(remainder));
        }

        return encodedResult.reverse().toString();
    }

}
