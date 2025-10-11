package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Application;
import com.quyhoang.flexistudy.enums.ApplicationStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, String> {
    boolean existsByUser_IdAndJob_Id(String userId, String jobId);

    List<Application> findByUser_Id(String userId);

    List<Application> findByJob_Id(String jobId);

    List<Application> findByStatus(ApplicationStatus status);

    Optional<Application> findByIdAndUser_Id(String id, String userId);
}
