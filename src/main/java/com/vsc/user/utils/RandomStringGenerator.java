package com.vsc.user.utils;

import java.util.UUID;

public class RandomStringGenerator {
    public static String generateToken() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
