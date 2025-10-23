package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Company;
import com.quyhoang.flexistudy.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company,String> {
    @Query("""
        SELECT c FROM Company c
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.website) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Company> search(@Param("keyword") String keyword, Pageable pageable);
    boolean existsByName(String name);
    Page<Company> findByVerificationStatus(VerificationStatus status, Pageable pageable);
}
