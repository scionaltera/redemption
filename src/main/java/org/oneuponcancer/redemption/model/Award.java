package org.oneuponcancer.redemption.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "event_participants")
public class Award {
    @EmbeddedId
    private AwardIdentity awardIdentity;

    @ManyToOne
    private Asset asset;

    public AwardIdentity getAwardIdentity() {
        return awardIdentity;
    }

    public void setAwardIdentity(AwardIdentity awardIdentity) {
        this.awardIdentity = awardIdentity;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Award)) return false;
        Award award = (Award) o;
        return Objects.equals(getAwardIdentity(), award.getAwardIdentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAwardIdentity());
    }
}
