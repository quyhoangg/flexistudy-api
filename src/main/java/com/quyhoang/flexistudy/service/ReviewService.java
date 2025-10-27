package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.ReviewCreateRequest;
import com.quyhoang.flexistudy.dto.request.ReviewUpdateRequest;
import com.quyhoang.flexistudy.dto.response.RatingSummaryResponse;
import com.quyhoang.flexistudy.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse create(String currentUserId, ReviewCreateRequest req);
    ReviewResponse update(String currentUserId, String reviewId, ReviewUpdateRequest req);
    void deleteOwn(String currentUserId, String reviewId);

    Page<ReviewResponse> listByCompany(String companyId, Pageable pageable);
    RatingSummaryResponse companyRatingSummary(String companyId);
}
