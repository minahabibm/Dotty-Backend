package com.tradingbot.dotty.utils;

import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionUtil implements AttributeConverter<String, String> {

    @Value("${encryption.algorithm}")
    private String ALGORITHM;
    @Value("${encryption.key}")
    private String secretKey;                                                                                           // 32 bytes (256-bit) key

    public SecretKey getSecretKey() {
//        byte[] keyBytes = Base64.getDecoder().decode(secretKey);                                                      // Base64
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute != null) {
            try {
                return encrypt(attribute);
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt data", e);
            }
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(String attribute) {
        if (attribute != null) {
            try {
                return decrypt(attribute);
            } catch (Exception e) {
                throw new RuntimeException("Failed to decrypt data", e);
            }
        }
        return null;
    }
}
