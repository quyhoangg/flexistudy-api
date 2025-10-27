package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Application;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.ApplicationStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, String> {
    boolean existsByUser_IdAndJob_Id(String userId, String jobId);
    List<Application> findByJob_Company_Id(String companyId);
    List<Application> findByUser_Id(String userId);

    List<Application> findByJob_Id(String jobId);

    List<Application> findByCompany_Id(String companyId);

    boolean existsByJob_Company_IdAndUser_IdAndStatus(
            String companyId,
            String userId,
            ApplicationStatus status
    );

    List<Application> findByJob(Job job);
    List<Application> findByUser(User user);
    Optional<Application> findByJobAndUser(Job job, User user);
}
