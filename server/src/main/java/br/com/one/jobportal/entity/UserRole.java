package br.com.one.jobportal.entity;

public enum UserRole {
    RECRUITER("Recrutador"),
    CANDIDATE("Candidato"),
    ADMIN("Administrador");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
