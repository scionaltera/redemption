package org.oneuponcancer.redemption.model.transport;

import java.util.UUID;

public class AwardAssetChangeRequest {
    private UUID assetId;

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }
}
