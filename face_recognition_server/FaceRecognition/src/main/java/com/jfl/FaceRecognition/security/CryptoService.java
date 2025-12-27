package com.jfl.FaceRecognition.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class CryptoService {

    private static final int GCM_TAG_LENGTH = 128;
    private static final int NONCE_LENGTH = 12;

    private final byte[] aesKey;

    public CryptoService(@Value("${face.aes-key}") String base64Key) {
        this.aesKey = Base64.getDecoder().decode(base64Key);
        if (aesKey.length != 32) {
            throw new IllegalArgumentException("AES key must be 32 bytes");
        }
    }

    public byte[] decrypt(String base64Payload) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(base64Payload);

            byte[] nonce = new byte[NONCE_LENGTH];
            byte[] cipherText = new byte[encrypted.length - NONCE_LENGTH];

            System.arraycopy(encrypted, 0, nonce, 0, NONCE_LENGTH);
            System.arraycopy(encrypted, NONCE_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
            return cipher.doFinal(cipherText);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt payload", e);
        }
    }
}
