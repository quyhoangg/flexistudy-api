package com.quyhoang.flexistudy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOSCreateRequest {
    private Long orderCode;
    private Long amount;
    private String description;
    private String cancelUrl;
    private String returnUrl;
    private String signature;
}
