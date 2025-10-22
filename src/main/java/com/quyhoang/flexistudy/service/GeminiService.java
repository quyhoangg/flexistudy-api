package com.quyhoang.flexistudy.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.Part;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.dto.request.ChatRequest;
import com.quyhoang.flexistudy.dto.response.ChatResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeminiService {

    Client client;

    //Khởi tạo client Gemini với API key từ cấu hình.
    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .httpOptions(HttpOptions.builder().apiVersion("v1").build())
                .build();
    }

    // Gửi prompt đến Gemini model và trả về phản hồi dạng văn bản.
    public ChatResponse generateResponse(ChatRequest request) {
        // Kiểm tra request rỗng/null
        if (request == null || request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            throw new AppException(ErrorCode.GEMINI_API_ERROR);
        }

        try {
            // Chuẩn bị nội dung gửi đến model Gemini
            Content content = Content.builder()
                    .parts(Collections.singletonList(Part.fromText(request.getPrompt().trim())))
                    .build();

            // Gọi API Gemini
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.0-flash", // model ổn định và nhanh
                    content,
                    null
            );
            // Kiểm tra phản hồi hợp lệ
            if (response == null || response.text() == null) {
                throw new AppException(ErrorCode.GEMINI_API_ERROR);
            }
            // Trả kết quả
            return new ChatResponse(response.text(), LocalDateTime.now().toString());
        } catch (Exception e) {
            // Nếu có lỗi từ phía Gemini hoặc network
            throw new AppException(ErrorCode.GEMINI_API_ERROR);
        }
    }
}