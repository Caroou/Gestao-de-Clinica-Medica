package com.clinicamedica.util;

import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SegurancaHelper {
    
    // A chave precisa ter exatamente 16 caracteres para o AES-128 funcionar.
    // Numa aplicação real em produção, isto ficaria escondido numa variável de ambiente.
    private static final String CHAVE_SECRETA = "Cl1n1c4M3d1c4K3y"; 
    private static final String ALGORITMO = "AES";

    // ==========================================
    // 1. SENHAS (Mão Única - BCrypt)
    // ==========================================
    
    public static String gerarHashSenha(String senha) {
        // Transforma a senha "1234" num hash irreversível
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public static boolean verificarSenha(String senhaDigitada, String hashDoBanco) {
        // Compara a senha que o utilizador digitou com o hash maluco que está na base de dados
        return BCrypt.checkpw(senhaDigitada, hashDoBanco);
    }

    // ==========================================
    // 2. DADOS SENSÍVEIS (Mão Dupla - AES)
    // ==========================================
    
    public static String criptografar(String dadoSensivel) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), ALGORITMO);
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] dadoCriptografado = cipher.doFinal(dadoSensivel.getBytes());
            // Converte para Base64 para poder ser guardado como texto na base de dados
            return Base64.getEncoder().encodeToString(dadoCriptografado);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar dado: " + e.getMessage());
        }
    }

    public static String descriptografar(String dadoCriptografado) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), ALGORITMO);
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] dadoDecodificado = Base64.getDecoder().decode(dadoCriptografado);
            byte[] dadoOriginal = cipher.doFinal(dadoDecodificado);
            return new String(dadoOriginal);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar dado: " + e.getMessage());
        }
    }
}