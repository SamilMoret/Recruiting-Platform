package br.com.one.jobportal.converter;

import br.com.one.jobportal.entity.EmploymentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmploymentTypeConverter implements AttributeConverter<EmploymentType, String> {

    @Override
    public String convertToDatabaseColumn(EmploymentType employmentType) {
        if (employmentType == null) {
            return EmploymentType.FULL_TIME.getDisplayName();
        }
        return employmentType.getDisplayName();
    }

    @Override
    public EmploymentType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return EmploymentType.FULL_TIME;
        }
        
        // Tenta converter a string do banco para o enum
        for (EmploymentType type : EmploymentType.values()) {
            if (type.getDisplayName().equalsIgnoreCase(dbData.trim())) {
                return type;
            }
        }
        
        // Se não encontrar, retorna o valor padrão
        return EmploymentType.FULL_TIME;
    }
}
