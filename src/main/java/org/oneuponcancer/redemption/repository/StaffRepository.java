package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends MongoRepository<Staff, String> {
    Staff findByUsername(String username);
}
