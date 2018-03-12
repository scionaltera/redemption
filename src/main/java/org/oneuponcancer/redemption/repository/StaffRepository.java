package org.oneuponcancer.redemption.repository;

import org.oneuponcancer.redemption.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    Staff findByUsername(String username);
}
