package com.example.taskflow.model.enums;

public enum UserRole {
    ADMIN("administrateur"),
    SUPERVISER("super viseur"),
    MEMEBRE_EQUIPE("memebre equipe"),
    ;
    public final String Value;

    UserRole(String value) {
        Value = value;
    }
}
