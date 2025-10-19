package com.quyhoang.flexistudy.service;


import com.quyhoang.flexistudy.dto.request.RecruiterRegistrationRequest;
import com.quyhoang.flexistudy.dto.response.RecruiterResponse;

public interface RecruiterRegistrationService {
    RecruiterResponse registerRecruiter(RecruiterRegistrationRequest request);
}
