package br.com.one.jobportal.converter;

import br.com.one.jobportal.entity.User.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        // Salva em minúsculas no banco de dados
        return role.name().toLowerCase();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return Role.JOB_SEEKER; // Valor padrão
        }
        // Converte de minúsculas (banco) para o enum
        return Role.fromString(dbData);
    }
}
