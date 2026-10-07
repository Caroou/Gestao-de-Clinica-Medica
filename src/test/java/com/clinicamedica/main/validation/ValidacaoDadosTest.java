package com.clinicamedica.main.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacaoDadosTest {
    @Test
    void aceitaCpfDeTesteComOnzeDigitosMesmoSemDigitosVerificadoresValidos() {
        assertEquals("00000000000", ValidacaoDados.cpf("00000000000"));
    }

    @Test
    void rejeitaCpfComQuantidadeOuCaracteresIncorretos() {
        assertThrows(IllegalArgumentException.class, () -> ValidacaoDados.cpf("1234567890"));
        assertThrows(IllegalArgumentException.class, () -> ValidacaoDados.cpf("000.000.000-00"));
    }

    @Test
    void rejeitaTelefoneComCaracteresDeMascaraNoPayload() {
        assertThrows(IllegalArgumentException.class, () -> ValidacaoDados.telefone("(11) 99999-9999"));
        assertEquals("11999999999", ValidacaoDados.telefone("11999999999"));
    }
}
