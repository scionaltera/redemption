package org.oneuponcancer.redemption.model;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AwardIdentity implements Serializable {
    @ManyToOne
    private Event event;

    @ManyToOne
    @JoinColumn(name = "participants_id")
    private Participant participant;

    public AwardIdentity() {
        // this method intentionally left blank
    }

    public AwardIdentity(Event event, Participant participant) {
        this.event = event;
        this.participant = participant;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AwardIdentity)) return false;
        AwardIdentity that = (AwardIdentity) o;
        return Objects.equals(getEvent(), that.getEvent()) &&
                Objects.equals(getParticipant(), that.getParticipant());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEvent(), getParticipant());
    }
}
