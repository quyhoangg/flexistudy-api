package com.quyhoang.flexistudy.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "Prompt không được để trống")
    @Size(max = 1000, message = "Prompt không được vượt quá 1000 ký tự")
    private String prompt;

}