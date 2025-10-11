package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education, UUID> {
    List<Education> findByUserId(String userId);
    void deleteByUserId(String userId);
}
