package com.fintech.framework.utils;

import com.github.javafaker.Faker;

import java.util.UUID;

public final class FintechDataFactory {

    private static final Faker FAKER = new Faker();

    private FintechDataFactory() {}

    public static String getRandomFirstName() {
        return FAKER.name().firstName();
    }

    public static String getRandomLastName() {
        return FAKER.name().lastName();
    }

    public static String getRandomEmail() {
        return "qa." + UUID.randomUUID().toString().substring(0, 8) + "@fintechtest.com";
    }

    public static String getValidVisaCardNumber() {
        return LuhnAlgorithm.generateValidCardNumber("4111", 16);
    }

    public static String getValidMastercardCardNumber() {
        return LuhnAlgorithm.generateValidCardNumber("5500", 16);
    }

    public static String getRandomCvv() {
        return String.format("%03d", FAKER.number().numberBetween(100, 999));
    }

    public static String getRandomExpiryDate() {
        return String.format("%02d/%d", FAKER.number().numberBetween(1, 12), FAKER.number().numberBetween(2027, 2032));
    }

    public static String getRandomPin() {
        return String.format("%04d", FAKER.number().numberBetween(1000, 9999));
    }
}
