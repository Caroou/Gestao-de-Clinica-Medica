package com.clinicamedica.main.validation;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class ValidacaoDados {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern TELEFONE = Pattern.compile("^\\d{10,11}$");

    private ValidacaoDados() { }

    public static String textoObrigatorio(String valor, String campo, int limite) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Informe " + campo + ".");
        String normalizado = valor.trim();
        if (normalizado.length() > limite) throw new IllegalArgumentException(campo + " deve ter até " + limite + " caracteres.");
        return normalizado;
    }

    public static String telefone(String valor) {
        if (valor == null || !TELEFONE.matcher(valor).matches()) {
            throw new IllegalArgumentException("Informe telefone com DDD, usando somente 10 ou 11 números.");
        }
        return valor;
    }

    public static String cpf(String valor) {
        if (valor == null || !valor.matches("\\d{11}")) {
            throw new IllegalArgumentException("Informe um CPF de teste com exatamente 11 números.");
        }
        return valor;
    }

    public static String email(String valor) {
        String email = textoObrigatorio(valor, "um e-mail", 255);
        if (!EMAIL.matcher(email).matches()) throw new IllegalArgumentException("Informe um e-mail válido.");
        return email;
    }

    public static int bytesUtf8(String valor) {
        return valor == null ? 0 : valor.getBytes(StandardCharsets.UTF_8).length;
    }
}
