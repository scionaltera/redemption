package org.oneuponcancer.redemption.model.transport;

import javax.validation.constraints.Size;

public class AssetEditRequest {
    @Size(min = 3, message = "Names must be at least 3 letters long.")
    private String name;

    @Size(min = 3, message = "Descriptions must be at least 3 letters long.")
    private String description;

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
}
