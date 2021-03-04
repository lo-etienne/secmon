package be.flmr.secmon.core.security;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESUtils {
    private static final boolean ENCRYPTION = false;

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY = "aPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq";

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    public static synchronized byte[] encrypt(String in, String strKey) {
        if (!ENCRYPTION) return in.getBytes(StandardCharsets.UTF_8);
        try {
            return initCipher(strKey, Cipher.ENCRYPT_MODE).doFinal(in.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Il y a eu une exception lors de l'encryption");
        }
    }

    public static synchronized String decrypt(byte[] in, String strKey) {
        if (!ENCRYPTION) return new String(in, StandardCharsets.UTF_8);
        try {
            return new String(initCipher(strKey, Cipher.DECRYPT_MODE).doFinal(in));
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Il y a eu une exception lors de la décryption");
        }
    }

    public static synchronized Cipher initCipher(String strKey, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        var key = generateKey(strKey);
        var IV = generateIV();

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        cipher.init(mode, keySpec, gcmParameterSpec);
        return cipher;
    }

    private static SecretKey generateKey(String key) {
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
    }

    private static byte[] generateIV() {
        byte[] IV = new byte[GCM_IV_LENGTH];
        for (int i = 0; i < IV.length; i++) IV[i] = (byte) i;
        return IV;
    }
}
