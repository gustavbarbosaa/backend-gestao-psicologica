package br.com.gestaopsicologica.security.criptografia;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Converter
@RequiredArgsConstructor
public class EvolucaoCriptografiaConverter implements AttributeConverter<String, String> {
    private final CriptografiaService criptografiaService;

    @Override
    public String convertToDatabaseColumn(String atributo) {
        if (atributo == null) {
            return null;
        }

        return criptografiaService.encriptografa(atributo);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return criptografiaService.descriptografa(dbData);
    }
}
