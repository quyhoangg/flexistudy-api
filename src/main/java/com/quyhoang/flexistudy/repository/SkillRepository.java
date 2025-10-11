package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill,String> {
    Optional<Skill> findByNameIgnoreCase(String name);
    Optional<Skill> findByName(String name);
    List<Skill> findByNameContainingIgnoreCase(String keyword);
    @Query("SELECT s FROM Skill s WHERE LOWER(TRIM(s.name)) LIKE LOWER(CONCAT('%', TRIM(:keyword), '%'))")
    List<Skill> searchByKeyword(@Param("keyword") String keyword);

}
