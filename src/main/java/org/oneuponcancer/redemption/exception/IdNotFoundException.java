package org.oneuponcancer.redemption.exception;

import java.util.UUID;

public class IdNotFoundException extends RuntimeException {
    private UUID id;

    public IdNotFoundException(UUID id, String message) {
        super(message);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
