package com.example.library54.repository;

import com.example.library54.domain.Role;
import com.example.library54.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleType name);

    @Query("select roleCount from Role where id=1")
    Long countOfMember();
}
