package com.quyhoang.flexistudy.dto.request;

import lombok.Data;

@Data
public class PayOSCallbackRequest {
    private String orderCode;
    private String status;
    private Long amount;
    private String transactionId;

    private String userId;
    private Long planId;
}
