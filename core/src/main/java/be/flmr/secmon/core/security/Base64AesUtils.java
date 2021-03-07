package be.flmr.secmon.core.security;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Base64AesUtils {
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    public static synchronized String encrypt(String input, String key) {
        try {
            return Base64.getEncoder().encodeToString(initCipher(key, Cipher.ENCRYPT_MODE).doFinal(input.getBytes(StandardCharsets.UTF_8))) + "\r\n";
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Il y a eu une exception lors de l'encryption");
        }
    }

    public static synchronized String decrypt(String encrypted, String key) {
        try {
            return new String(initCipher(key, Cipher.DECRYPT_MODE).doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Il y a eu une exception lors de la décryption");
        }
    }

    private static synchronized Cipher initCipher(String keyStr, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        var key = generateKey(keyStr);
        var IV = generateInitializationVector();

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        cipher.init(mode, keySpec, gcmParameterSpec);
        return cipher;

    }

    private static SecretKey generateKey(String keyStr) {
        return new SecretKeySpec(keyStr.getBytes(StandardCharsets.UTF_8), "AES");
    }

    private static byte[] generateInitializationVector() {
        byte[] IV = new byte[GCM_IV_LENGTH];

        for (int i = 0; i < IV.length; i++) IV[i] = (byte) i;

        return IV;
    }
}