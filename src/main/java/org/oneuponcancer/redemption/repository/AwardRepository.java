package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.Award;
import org.oneuponcancer.redemption.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AwardRepository extends JpaRepository<Award, UUID> {
    List<Award> findByAwardIdentity_Event(Event event);
}
