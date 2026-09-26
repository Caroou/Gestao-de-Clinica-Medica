package com.clinicamedica.main.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class CriptografiaAesConverter implements AttributeConverter<String, String> {

    // Chave secreta de 16 caracteres.
    private static final String CHAVE_SECRETA = "ChaveClinica1234";
    private static final String ALGORITMO = "AES";

    @Override
    public String convertToDatabaseColumn(String atributoDoJava) {
        if (atributoDoJava == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            SecretKeySpec key = new SecretKeySpec(CHAVE_SECRETA.getBytes(), ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] bytesCriptografados = cipher.doFinal(atributoDoJava.getBytes());
            return Base64.getEncoder().encodeToString(bytesCriptografados);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dadoDoBanco) {
        if (dadoDoBanco == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            SecretKeySpec key = new SecretKeySpec(CHAVE_SECRETA.getBytes(), ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] bytesDescriptografados = cipher.doFinal(Base64.getDecoder().decode(dadoDoBanco));
            return new String(bytesDescriptografados);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar", e);
        }
    }
}