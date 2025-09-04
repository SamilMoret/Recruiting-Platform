package br.com.one.jobportal.entity;

public enum ExperienceLevel {
    ENTRY("Iniciante"),
    JUNIOR("Júnior"),
    MID("Pleno"),
    SENIOR("Sênior"),
    LEAD("Liderança");

    private final String description;

    ExperienceLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
