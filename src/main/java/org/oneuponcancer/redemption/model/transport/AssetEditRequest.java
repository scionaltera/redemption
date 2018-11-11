package org.oneuponcancer.redemption.model.transport;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public class AssetEditRequest {
    @Size(min = 3, message = "Names must be at least 3 letters long.")
    private String name;

    @Size(min = 3, message = "Descriptions must be at least 3 letters long.")
    private String description;

    @NotNull(message = "Event must be selected.")
    private UUID eventId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
}
