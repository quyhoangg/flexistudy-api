package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.configuration.SecurityUtils;
import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.request.NewsCreateRequest;
import com.quyhoang.flexistudy.dto.request.NewsUpdateRequest;
import com.quyhoang.flexistudy.dto.response.NewsResponse;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.service.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsController {
    NewsService newsService;
    SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ApiResponse<NewsResponse> createNews(
            @Valid @RequestBody NewsCreateRequest request,
            HttpServletRequest httpRequest) {

        String authorId = securityUtils.extractUserIdFromToken(httpRequest);
        if (authorId == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return ApiResponse.<NewsResponse>builder()
                .code(1000)
                .message("Success")
                .result(newsService.create(request, authorId))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ApiResponse<NewsResponse> updateNews(@PathVariable String id,
                                                @Valid @RequestBody NewsUpdateRequest request) {
        return ApiResponse.<NewsResponse>builder()
                .code(1000)
                .message("Success")
                .result(newsService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteNews(@PathVariable String id) {
        newsService.delete(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Deleted successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<NewsResponse> getNewsById(@PathVariable String id) {
        return ApiResponse.<NewsResponse>builder()
                .code(1000)
                .message("Success")
                .result(newsService.getById(id))
                .build();
    }

    @GetMapping
    public PageResponse<NewsResponse> getAllNews(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category // thêm
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = "USER";

        if (authentication != null && authentication.getAuthorities() != null) {
            role = authentication.getAuthorities().iterator().next().getAuthority();
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
        }

        int actualPage = Math.max(pageable.getPageNumber() - 1, 0);
        Pageable fixedPageable = PageRequest.of(actualPage, pageable.getPageSize(), pageable.getSort());

        Page<NewsResponse> page = newsService.getAll(fixedPageable, keyword, status, category, role);

        return PageResponse.<NewsResponse>builder()
                .currentPage(actualPage + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .data(page.getContent())
                .build();
    }

    @PostMapping("/upload-image/{newsId}")
    public ApiResponse<String> uploadImgNews(
            @PathVariable String newsId,
            @RequestParam("file") MultipartFile file
    ) {
        String fileUrl = newsService.uploadImgNews(newsId, file);
        return ApiResponse.<String>builder()
                .result(fileUrl)
                .message("News image uploaded successfully")
                .build();
    }
}
