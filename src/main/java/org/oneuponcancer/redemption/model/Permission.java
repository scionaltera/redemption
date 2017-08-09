package org.oneuponcancer.redemption.model;

public enum Permission {
    LOGIN("Can log in");

    private String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
