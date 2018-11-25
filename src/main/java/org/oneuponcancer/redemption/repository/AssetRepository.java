package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.Asset;
import org.oneuponcancer.redemption.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByEvent(Event event);
}
