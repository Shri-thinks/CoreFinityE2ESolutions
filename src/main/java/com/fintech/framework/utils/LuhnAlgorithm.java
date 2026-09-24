package com.fintech.framework.utils;

import java.util.Random;

public final class LuhnAlgorithm {

    private static final Random RANDOM = new Random();

    private LuhnAlgorithm() {}

    public static String generateValidCardNumber(String binPrefix, int length) {
        StringBuilder builder = new StringBuilder(binPrefix);
        while (builder.length() < length - 1) {
            builder.append(RANDOM.nextInt(10));
        }
        int checkDigit = calculateCheckDigit(builder.toString());
        builder.append(checkDigit);
        return builder.toString();
    }

    public static int calculateCheckDigit(String numberWithoutCheckDigit) {
        int sum = 0;
        boolean alternate = true;
        for (int i = numberWithoutCheckDigit.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numberWithoutCheckDigit.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum * 9) % 10;
    }

    public static boolean isValidCardNumber(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
