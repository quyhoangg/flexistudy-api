package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByConversationIdOrderBySentAtAsc(Long conversationId);
}
