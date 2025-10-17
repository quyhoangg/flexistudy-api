package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {
    Optional<VerificationCode> findByEmailAndCode(String email, String code);
}
