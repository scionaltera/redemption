package org.oneuponcancer.redemption.model;

public enum Permission {
    LOGIN("login", "Can log in"),
    READ_LOGS("read-logs", "Can read logs"),
    CREATE_STAFF("create-staff", "Can create staff accounts"),
    LIST_STAFF("list-staff", "Can list staff accounts"),
    EDIT_STAFF("edit-staff", "Can edit staff accounts"),
    DELETE_STAFF("delete-staff", "Can delete staff accounts"),
    CREATE_ASSET("create-asset", "Can create assets"),
    LIST_ASSET("list-asset", "Can list assets"),
    EDIT_ASSET("edit-asset", "Can edit assets"),
    DELETE_ASSET("delete-asset", "Can delete assets");

    private String unique;
    private String description;

    Permission(String unique, String description) {
        this.unique = unique;
        this.description = description;
    }

    public String getUnique() {
        return unique;
    }

    public String getDescription() {
        return description;
    }
}
