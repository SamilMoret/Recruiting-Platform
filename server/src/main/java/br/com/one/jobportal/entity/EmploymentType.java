package br.com.one.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmploymentType {
    FULL_TIME("Full-Time"),
    PART_TIME("Part-Time"),
    CONTRACT("Contract"),
    REMOTE("Remote"),
    INTERNSHIP("Internship");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @JsonCreator
    public static EmploymentType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return FULL_TIME;
        }
        
        // Primeiro tenta encontrar pelo displayName (ex: "Full-Time")
        for (EmploymentType type : values()) {
            if (type.displayName.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        
        // Se não encontrar pelo displayName, tenta pelo nome do enum (ex: "FULL_TIME")
        try {
            // Remove hífens e converte para maiúsculas para tentar fazer o match
            String enumName = value.trim().toUpperCase().replace("-", "_");
            return EmploymentType.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            // Se não encontrar de nenhuma forma, retorna o valor padrão
            return FULL_TIME;
        }
    }
}