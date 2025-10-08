package br.com.one.jobportal.entity;

public enum UserRole {
    EMPLOYER("Empregador"),
    JOB_SEEKER("Candidato"),
    ADMIN("Administrador");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromString(String role) {
        if (role == null) {
            return JOB_SEEKER;
        }
        String key = role.trim().toUpperCase().replace(" ", "_");
        try {
            return UserRole.valueOf(key);
        } catch (IllegalArgumentException e) {
            // Para compatibilidade com valores antigos
            if (key.equals("RECRUITER")) return EMPLOYER;
            if (key.equals("CANDIDATE")) return JOB_SEEKER;
            return JOB_SEEKER; // valor padrão
        }
    }
}
