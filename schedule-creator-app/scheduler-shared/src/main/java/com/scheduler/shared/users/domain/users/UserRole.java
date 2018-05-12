package com.scheduler.shared.users.domain.users;

import javax.persistence.Embeddable;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
public enum UserRole {

    TENANT_ADMIN("Administrator"),

    MANAGEMENT("Dyrekcja"),
    TEACHER("Nauczyciel");

    private String value;

    UserRole(final String value) {
        this.value = value;
    }    @Override
    public String toString() {
        return value;
    }
}
