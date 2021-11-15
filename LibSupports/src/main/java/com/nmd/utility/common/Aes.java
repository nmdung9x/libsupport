package com.nmd.utility.common;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Aes {

    static final String ALGORITHM = "AES";
    static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";


    public static SecretKey generateKey(String key) {
        return new SecretKeySpec(key.getBytes(), ALGORITHM);
    }

    public static SecretKey generateKey(byte[] pass_key) {
        return new SecretKeySpec(pass_key, ALGORITHM);
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    public static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        if (input.trim().isEmpty()) return "";
        Cipher cipher = Cipher.getInstance(algorithm);
        if (iv == null) cipher.init(Cipher.ENCRYPT_MODE, key);
        else cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    public static String encrypt(String input, SecretKey key, IvParameterSpec iv) {
        try {
            return encrypt(ALGORITHM, input, key, iv);
        } catch (Exception ignored) {}
        return "";
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        if (cipherText.trim().isEmpty()) return "";
        Cipher cipher = Cipher.getInstance(algorithm);
        if (iv == null) cipher.init(Cipher.DECRYPT_MODE, key);
        else cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT));
        return new String(plainText);
    }

    public static String decrypt(String cipherText, SecretKey key, IvParameterSpec iv) {
        try {
            return decrypt(ALGORITHM, cipherText, key, iv);
        } catch (Exception ignored) {}
        return "";
    }
}
