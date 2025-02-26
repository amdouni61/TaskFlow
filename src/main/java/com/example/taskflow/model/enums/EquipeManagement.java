package com.example.taskflow.model.enums;

public enum EquipeManagement {
    MODERNISATION_IT_ET_DESIGNATION("CDD", "COUCHE_INTEGRATION", "DATA", "DEVELOPPEMENT_MONETIQUE", "DEVOPS", "DIGITAL", "FONCTIONNEL", "TEST_FACTORY"),
    PMO_IT(),
    RESILIENCE_IT("ARCHITECTURE_IT", "DAS", "PCI", "PRODUCTION_IT", "RESEAU", "SECURITE");

    private final String[] teams;

    EquipeManagement(String... teams) {
        this.teams = teams;
    }

    public String[] getTeams() {
        return teams;
    }
}

