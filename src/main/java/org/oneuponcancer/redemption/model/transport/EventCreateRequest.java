package org.oneuponcancer.redemption.model.transport;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventCreateRequest {
    @Size(min = 3, message = "Names must be at least 3 letters long.")
    private String name;

    @Size(min = 3, message = "Descriptions must be at least 3 letters long.")
    private String description;

    @FutureOrPresent
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date startDate;

    @FutureOrPresent
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date endDate;

    private List<UUID> participants;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<UUID> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UUID> participants) {
        this.participants = participants;
    }
}
