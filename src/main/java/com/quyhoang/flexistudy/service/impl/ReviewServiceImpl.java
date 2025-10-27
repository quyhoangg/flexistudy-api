package com.quyhoang.flexistudy.service.impl;

import com.quyhoang.flexistudy.dto.request.ReviewCreateRequest;
import com.quyhoang.flexistudy.dto.request.ReviewUpdateRequest;
import com.quyhoang.flexistudy.dto.response.RatingSummaryResponse;
import com.quyhoang.flexistudy.dto.response.ReviewResponse;
import com.quyhoang.flexistudy.entity.Company;
import com.quyhoang.flexistudy.entity.Review;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.ApplicationStatus;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.mapper.ReviewMapper;
import com.quyhoang.flexistudy.repository.ApplicationRepository;
import com.quyhoang.flexistudy.repository.CompanyRepository;
import com.quyhoang.flexistudy.repository.ReviewRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import com.quyhoang.flexistudy.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {

    ReviewRepository reviewRepository;
    UserRepository userRepository;
    CompanyRepository companyRepository;
    ReviewMapper reviewMapper;
    ApplicationRepository applicationRepository;

    boolean REQUIRE_APPLIED_BEFORE_REVIEW = true;

    @Override
    @Transactional
    public ReviewResponse create(String currentUserId, ReviewCreateRequest req) {
        // Load user & check role
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isCandidate = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.USER);

        if (!isCandidate) {
            throw new IllegalStateException("Only Candidates can write reviews!");
        }

        // Load company
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        // Must be hired before reviewing
        if (REQUIRE_APPLIED_BEFORE_REVIEW) {
            boolean hasBeenHired = applicationRepository
                    .existsByJob_Company_IdAndUser_IdAndStatus(company.getId(), user.getId(), ApplicationStatus.HIRED);

            if (!hasBeenHired) {
                throw new IllegalStateException("You can only review companies where you have been hired.");
            }
        }

        // Only 1 review per company
        if (reviewRepository.existsByCompany_IdAndUser_Id(company.getId(), user.getId())) {
            throw new IllegalStateException("You already reviewed this company. Please update instead.");
        }

        // Persist
        Review r = Review.builder()
                .company(company)
                .user(user)
                .star(req.getStar())
                .comment(StringUtils.hasText(req.getComment()) ? req.getComment().trim() : null)
                .build();

        r = reviewRepository.save(r);
        reviewRepository.flush(); // force timestamp + lazy loading

        // reload to ensure timestamps + relations are populated
        r = reviewRepository.findById(r.getId())
                .orElseThrow(() -> new IllegalStateException("Review reload failed"));

        return reviewMapper.toResponse(r);
    }

    @Override
    @Transactional
    public ReviewResponse update(String currentUserId, String reviewId, ReviewUpdateRequest req) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!r.getUser().getId().equals(currentUserId)) {
            throw new IllegalStateException("You can only update your own review.");
        }

        r.setStar(req.getStar());
        r.setComment(StringUtils.hasText(req.getComment()) ? req.getComment().trim() : null);

        r = reviewRepository.save(r);
        reviewRepository.flush();

        //  Reload to update timestamp & lazy fields
        r = reviewRepository.findById(r.getId()).get();

        return reviewMapper.toResponse(r);
    }

    @Override
    @Transactional
    public void deleteOwn(String currentUserId, String reviewId) {
        int affected = reviewRepository.deleteByIdAndUserId(reviewId, currentUserId);
        if (affected == 0) {
            throw new IllegalStateException("Review not found or unauthorized deletion");
        }
    }

    @Override
    public Page<ReviewResponse> listByCompany(String companyId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByCompany_IdOrderByCreatedAtDesc(companyId, pageable);
        return page.map(reviewMapper::toResponse);
    }

    @Override
    public RatingSummaryResponse companyRatingSummary(String companyId) {
        return RatingSummaryResponse.builder()
                .totalReviews(reviewRepository.countByCompany_Id(companyId))
                .averageStar(reviewRepository.averageStarByCompany(companyId))
                .build();
    }
}

