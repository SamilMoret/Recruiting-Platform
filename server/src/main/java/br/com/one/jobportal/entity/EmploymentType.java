package br.com.one.jobportal.entity;

public enum EmploymentType {
    FULL_TIME("Tempo Integral"),
    PART_TIME("Meio Período"),
    CONTRACT("Contrato"),
    INTERNSHIP("Estágio");

    private final String description;

    EmploymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}