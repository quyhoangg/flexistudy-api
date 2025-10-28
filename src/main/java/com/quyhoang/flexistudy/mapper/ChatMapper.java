package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.response.ChatMessageResponse;
import com.quyhoang.flexistudy.dto.response.ConversationResponse;
import com.quyhoang.flexistudy.entity.ChatMessage;
import com.quyhoang.flexistudy.entity.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(source = "conversation.id", target = "conversationId")
    ChatMessageResponse toMessageResponse(ChatMessage message);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "company.id", target = "companyId")
    ConversationResponse toConversationResponse(Conversation conversation);
}
