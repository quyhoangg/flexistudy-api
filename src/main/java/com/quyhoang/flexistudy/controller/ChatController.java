package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.request.ChatRequest;
import com.quyhoang.flexistudy.dto.response.ChatResponse;
import com.quyhoang.flexistudy.service.GeminiService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    GeminiService geminiService;

    @PostMapping
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse chatResponse = geminiService.generateResponse(request);
        return ApiResponse.<ChatResponse>builder()
                .result(chatResponse)
                .message("Chat response generated successfully")
                .build();
    }
}