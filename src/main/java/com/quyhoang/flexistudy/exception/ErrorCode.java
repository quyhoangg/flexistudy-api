package com.quyhoang.flexistudy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid Key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User is not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at leat {min}", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND(1009, "File not found", HttpStatus.NOT_FOUND),
    INVALID_REQUEST(1010, "Invalid Request", HttpStatus.BAD_REQUEST),
    COMPANY_NOT_FOUND(1011, "Company not found", HttpStatus.NOT_FOUND),
    JOB_NOT_FOUND(1011, "Job is not found", HttpStatus.NOT_FOUND),
    SKILL_NOT_FOUND(1012, "Skill not found", HttpStatus.NOT_FOUND),
    JOB_REQUIRED_EXISTED(1013, "Job required is existed", HttpStatus.BAD_REQUEST),
    JOB_SKILL_REQUIRED_IS_NOT_FOUND(1014, "Job required Skill is not found", HttpStatus.NOT_FOUND),
    SKILL_EXISTED(1015, "Skill is existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1016, "Role is invalid", HttpStatus.BAD_REQUEST),
    APPLICATION_NOT_FOUND(1017, "Application is not found", HttpStatus.NOT_FOUND),
    APPLIED_ALREADY(1018, "You've apllied", HttpStatus.BAD_REQUEST),
    JOB_CLOSED(1019, "Job have closed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1020, "User is not found", HttpStatus.NOT_FOUND),
    JOB_NOT_SAVED(1021, "Job cannnot save", HttpStatus.BAD_REQUEST),
    JOB_ALREADY_SAVED(1022, "Job saved already", HttpStatus.BAD_REQUEST),
    AVAILABILITY_NOT_FOUND(1023, "Availability not found", HttpStatus.NOT_FOUND),
    SHIFT_NOT_FOUND(1024, "Shift not found", HttpStatus.NOT_FOUND),
    UPLOAD_FAILE(1025, "Upload file is faile", HttpStatus.BAD_REQUEST),
    RECRUITER_NO_COMPANY(1026, "User haven't been assign a company", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(1027, "Role is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_EXISTED(1028, "Password is existed", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1029, "OTP is invalid", HttpStatus.BAD_REQUEST),
    EXPIRED_OTP(1030, "OPT was expired", HttpStatus.BAD_REQUEST),
    NEWS_NOT_FOUND(1031, "NEWS_NOT_FOUND", HttpStatus.NOT_FOUND),
    AUTHOR_NOT_FOUND(1032, "AUTHOR_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_STATUS(1033, "INVALID_STATUS", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(1034, "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_FILE_TYPE(1035, "File was not supported", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(1035, "File too large", HttpStatus.BAD_REQUEST),
    NOT_FOUND(1035, "File not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1036, "Email already exist", HttpStatus.BAD_REQUEST),
    COMPANY_ALREADY_EXISTS(1037, "Company already exist", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
