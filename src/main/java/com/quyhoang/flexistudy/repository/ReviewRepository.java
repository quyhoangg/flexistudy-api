package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    boolean existsByCompany_IdAndUser_Id(String companyId, String userId);

    Page<Review> findByCompany_IdOrderByCreatedAtDesc(String companyId, Pageable pageable);

    long countByCompany_Id(String companyId);

    @Query("SELECT COALESCE(AVG(r.star), 0) FROM Review r WHERE r.company.id = :companyId")
    double averageStarByCompany(@Param("companyId") String companyId);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.id = :id AND r.user.id = :userId")
    int deleteByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
}
