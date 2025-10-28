package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    Optional<Conversation> findByUserIdAndCompanyId(String userId, String companyId);
    List<Conversation> findByUserIdOrCompanyId(String userId, String companyId);
}
