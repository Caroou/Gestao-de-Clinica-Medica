package com.clinicamedica.main.config;

import com.clinicamedica.main.model.StatusConsulta;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusConsultaConverter implements AttributeConverter<StatusConsulta, String> {

    @Override
    public String convertToDatabaseColumn(StatusConsulta status) {
        if (status == null) return null;
        return status == StatusConsulta.NAO_COMPARECEU ? "NÃO COMPARECEU" : status.name();
    }

    @Override
    public StatusConsulta convertToEntityAttribute(String valor) {
        if (valor == null) return null;
        return "NÃO COMPARECEU".equals(valor) ? StatusConsulta.NAO_COMPARECEU : StatusConsulta.valueOf(valor);
    }
}
