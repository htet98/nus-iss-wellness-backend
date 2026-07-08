package nus.iss.wellness.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

//author: Junior

public class HashUtil {

    private HashUtil() {
    }

    public static String sha512(String value) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-512");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}