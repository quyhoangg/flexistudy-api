package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, UUID> {
    List<Experience> findByUserId(String userId);
    void deleteByUserId(String userId);
}
