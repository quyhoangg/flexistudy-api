package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.UserCv;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCvRepository extends CrudRepository<UserCv, String> {
    List<UserCv> findByUserIdOrderByUploadedAtDesc(String userId);
    Optional<UserCv> findFirstByUserIdAndIsPrimaryTrue(String userId);
}
