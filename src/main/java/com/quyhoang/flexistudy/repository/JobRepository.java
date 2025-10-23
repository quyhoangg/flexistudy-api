package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

    Page<Job> findByTitleContainingIgnoreCaseOrCompany_NameContainingIgnoreCase(
            String titleKeyword,
            String companyKeyword,
            Pageable pageable
    );

    @Query("""
    SELECT j FROM Job j
    WHERE j.category = :category
      AND j.status = com.quyhoang.flexistudy.enums.JobStatus.OPEN
      AND j.postedAt >= :postedCutoff
      AND j.expiryDate <= :urgentDeadline
      AND (:city IS NULL OR LOWER(j.city) LIKE LOWER(CONCAT('%', :city, '%')))
    ORDER BY j.postedAt DESC
""")
    Page<Job> findUrgentJobsByCategory(@Param("category") com.quyhoang.flexistudy.enums.JobCategory category,
                                       @Param("postedCutoff") LocalDateTime postedCutoff,
                                       @Param("urgentDeadline") LocalDateTime urgentDeadline,
                                       @Param("city") String city,
                                       Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.status = com.quyhoang.flexistudy.enums.JobStatus.OPEN")
    Page<Job> findActiveOpenJobs(Pageable pageable);

    @Query("""
    SELECT j FROM Job j
    WHERE j.category = :category
      AND j.status = com.quyhoang.flexistudy.enums.JobStatus.OPEN
      AND j.postedAt >= :cutoff
      AND (:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(j.company.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:city IS NULL OR LOWER(j.city) LIKE LOWER(CONCAT('%', :city, '%')))
      AND (:type IS NULL OR j.type = :type)
      AND (:minSalary IS NULL OR j.minSalary >= :minSalary)
      AND (:maxSalary IS NULL OR j.maxSalary <= :maxSalary)
    ORDER BY j.postedAt DESC
""")
    Page<Job> findJobsByCategoryFilter(@Param("category") com.quyhoang.flexistudy.enums.JobCategory category,
                                       @Param("search") String search,
                                       @Param("city") String city,
                                       @Param("type") com.quyhoang.flexistudy.enums.EmployeeType type,
                                       @Param("minSalary") Integer minSalary,
                                       @Param("maxSalary") Integer maxSalary,
                                       @Param("cutoff") LocalDateTime cutoff,
                                       Pageable pageable);




    @Query("""
            SELECT j 
            FROM Job j 
            WHERE j.postedAt >= :cutoff
              AND LOWER(j.city) LIKE LOWER(CONCAT('%', :city, '%'))
            ORDER BY j.postedAt DESC
            """)
    Page<Job> findActiveJobsByCity(@Param("city") String city,
                                   @Param("cutoff") LocalDateTime cutoff,
                                   Pageable pageable);

    @Query("""
          SELECT j FROM Job j
          WHERE j.status = com.quyhoang.flexistudy.enums.JobStatus.OPEN
            AND j.postedAt >= :postedCutoff
            AND j.expiryDate <= :urgentDeadline
            AND (:city IS NULL OR LOWER(j.city) LIKE LOWER(CONCAT('%', :city, '%')))
          ORDER BY j.postedAt DESC
""")
    Page<Job> findUrgentJobs(@Param("postedCutoff") LocalDateTime postedCutoff,
                             @Param("urgentDeadline") LocalDateTime urgentDeadline,
                             @Param("city") String city,
                             Pageable pageable);



    @Query("""
        SELECT j 
        FROM Job j
        WHERE (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(j.company.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND LOWER(j.city) LIKE LOWER(CONCAT('%', :city, '%'))
          AND j.postedAt >= :cutoff
        ORDER BY j.postedAt DESC
        """)
    Page<Job> searchActiveJobsByCity(@Param("keyword") String keyword,
                                     @Param("city") String city,
                                     @Param("cutoff") LocalDateTime cutoff,
                                     Pageable pageable);



    @Query("""
        SELECT j 
        FROM Job j
        WHERE (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(j.company.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND j.postedAt >= :cutoff
        ORDER BY j.postedAt DESC
        """)
    Page<Job> searchActiveJobs(@Param("keyword") String keyword,
                               @Param("cutoff") LocalDateTime cutoff,
                               Pageable pageable);

    @Query("""
        SELECT j 
        FROM Job j 
        WHERE j.postedAt >= :thirtyDaysAgo
        ORDER BY j.postedAt DESC
        """)
    Page<Job> findActiveJobs(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo, Pageable pageable);


    @Query("SELECT j.category, COUNT(j) FROM Job j GROUP BY j.category")
    List<Object[]> countJobsByCategory();
}

