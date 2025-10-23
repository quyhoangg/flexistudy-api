package com.quyhoang.flexistudy.enums;

public enum VerificationStatus {
    UNVERIFIED,   // mặc định
    PENDING,      // đã nộp hồ sơ, chờ admin duyệt
    VERIFIED,     // đã xác minh
    REJECTED,     // từ chối (kèm lý do)
    SUSPENDED     // tạm khóa xác minh (phát hiện gian lận)
}
