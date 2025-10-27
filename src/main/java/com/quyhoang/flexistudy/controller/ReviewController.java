package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.configuration.JwtUtils;
import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.request.ReviewCreateRequest;
import com.quyhoang.flexistudy.dto.request.ReviewUpdateRequest;
import com.quyhoang.flexistudy.dto.response.RatingSummaryResponse;
import com.quyhoang.flexistudy.dto.response.ReviewResponse;
import com.quyhoang.flexistudy.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;
    JwtUtils jwtUtils;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(
            @RequestBody @Valid ReviewCreateRequest request,
            HttpServletRequest httpReq
    ) {
        String currentUserId = jwtUtils.getUserIdFromRequest(httpReq);
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.create(currentUserId, request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable String id,
            @RequestBody @Valid ReviewUpdateRequest request,
            HttpServletRequest httpReq
    ) {
        String currentUserId = jwtUtils.getUserIdFromRequest(httpReq);
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.update(currentUserId, id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteReview(
            @PathVariable String id,
            HttpServletRequest httpReq
    ) {
        String currentUserId = jwtUtils.getUserIdFromRequest(httpReq);
        reviewService.deleteOwn(currentUserId, id);
        return ApiResponse.<String>builder()
                .result("Review deleted successfully")
                .build();
    }

    @GetMapping("/company/{companyId}")
    public ApiResponse<PageResponse<ReviewResponse>> listByCompany(
            @PathVariable String companyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Page<ReviewResponse> reviewPage = reviewService.listByCompany(companyId, pageable);
        PageResponse<ReviewResponse> response = toPageResponse(reviewPage);

        return ApiResponse.<PageResponse<ReviewResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping("/company/{companyId}/summary")
    public ApiResponse<RatingSummaryResponse> ratingSummary(
            @PathVariable String companyId
    ) {
        return ApiResponse.<RatingSummaryResponse>builder()
                .result(reviewService.companyRatingSummary(companyId))
                .build();
    }

    // ✅ Convert Page → PageResponse
    private PageResponse<ReviewResponse> toPageResponse(Page<ReviewResponse> page) {
        return PageResponse.<ReviewResponse>builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .data(page.getContent())
                .build();
    }
}
