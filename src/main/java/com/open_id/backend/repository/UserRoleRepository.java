package com.open_id.backend.repository;

import com.open_id.backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    Optional<UserRole> findByName(String name);

    Optional<UserRole> findByUser_Sub(String sub);
}
