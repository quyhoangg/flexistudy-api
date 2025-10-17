package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Role;
import com.quyhoang.flexistudy.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleName name);
    List<Role> findByNameIn(List<RoleName> names);
    void deleteByName(RoleName name);
}

