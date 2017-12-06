package org.oneuponcancer.redemption.model;

public enum Permission {
    LOGIN("Can log in"),
    READ_LOGS("Can read logs"),
    CREATE_STAFF("Can create staff accounts"),
    LIST_STAFF("Can list staff accounts"),
    EDIT_STAFF("Can edit staff accounts"),
    DELETE_STAFF("Can delete staff accounts");

    private String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
