package com.vng.zing.pusheventmessage.common;

import com.vng.zing.stats.Profiler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author anhlh
 */
public class AESEncrypt {

    public static byte[] encrypt(String keyInStr, String dataToEncrypt)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException {

        byte[] keyInBinary = keyInStr.getBytes();
        byte[] vector_bytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            vector_bytes[i] = 0;
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyInBinary, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(vector_bytes);

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

        return c.doFinal(dataToEncrypt.getBytes("UTF-8"));

    }

    public static String decrypt(String keyInStr, byte[] dataToEncrypt)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Profiler.getThreadProfiler().push(AESEncrypt.class, "decrypt");
        try {
            byte[] keyInBinary = keyInStr.getBytes();

            byte[] vector_bytes = new byte[16];
            for (int i = 0; i < 16; i++) {
                vector_bytes[i] = 0;
            }

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyInBinary, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(vector_bytes);

            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
            byte[] encryptedData = c.doFinal(dataToEncrypt);
            return new String(encryptedData);
        } finally {
            Profiler.getThreadProfiler().pop(AESEncrypt.class, "decrypt");
        }
    }
}
