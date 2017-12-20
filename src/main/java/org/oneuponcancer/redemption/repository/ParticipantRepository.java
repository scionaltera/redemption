package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends MongoRepository<Participant, String> {
}
