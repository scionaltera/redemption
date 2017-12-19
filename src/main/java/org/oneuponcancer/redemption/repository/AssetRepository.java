package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends MongoRepository<Asset, String> {
}
