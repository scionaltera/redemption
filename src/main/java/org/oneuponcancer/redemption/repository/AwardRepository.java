package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.Award;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AwardRepository extends JpaRepository<Award, UUID> {
}
