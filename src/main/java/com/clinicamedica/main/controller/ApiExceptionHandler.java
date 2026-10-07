package com.clinicamedica.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarRequisicaoInvalida(IllegalArgumentException erro) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", erro.getMessage() == null ? "Requisição inválida." : erro.getMessage()));
    }
}
