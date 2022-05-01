package com.github.jenya705.cubicauth;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Jenya705
 */
@UtilityClass
public class PasswordEncryption {

    private final char[] CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final Random RANDOM = new SecureRandom();

    private final MessageDigest ALGORITHM;

    static {
        try {
            ALGORITHM = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String hashWithRandomSalt(String password) {
        StringBuilder saltBuilder = new StringBuilder(16);
        for (int i = 0; i < 16; ++i) {
            saltBuilder.append(CHARS[RANDOM.nextInt(CHARS.length)]);
        }
        return hash(password, saltBuilder.toString());
    }

    public String hash(String password, String salt) {
        return "$SHA$" + salt + "$" + applyAlgorithm(applyAlgorithm(password) + salt);
    }

    public boolean isPasswordsEqual(String password, String hash) {
        String[] line = hash.split("\\$");
        return line.length == 4 && MessageDigest.isEqual(
                hash.getBytes(StandardCharsets.UTF_8),
                hash(password, line[2]).getBytes(StandardCharsets.UTF_8)
        );
    }

    private String applyAlgorithm(String str) {
        ALGORITHM.reset();
        ALGORITHM.update(str.getBytes());
        byte[] digest = ALGORITHM.digest();
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
    }

}
