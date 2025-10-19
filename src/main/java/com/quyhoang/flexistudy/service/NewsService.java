package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.NewsCreateRequest;
import com.quyhoang.flexistudy.dto.request.NewsUpdateRequest;
import com.quyhoang.flexistudy.dto.response.NewsResponse;
import com.quyhoang.flexistudy.entity.News;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.ContentStatus;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.NewsMapper;
import com.quyhoang.flexistudy.repository.NewsRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsService {
    NewsRepository newsRepository;
    UserRepository userRepository;
    NewsMapper newsMapper;
    FileStorageService fileStorageService;

    public NewsResponse create(NewsCreateRequest request, String authorId) {
        if (authorId == null || authorId.isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

        News news = newsMapper.toEntity(request);
        news.setAuthor(author);

        //  Nếu category trống → gán "Uncategorized"
        if (news.getCategory() == null || news.getCategory().isBlank()) {
            news.setCategory("Uncategorized");
        }

        // Nếu status trống → mặc định là DRAFT
        if (news.getStatus() == null) {
            news.setStatus(ContentStatus.DRAFT);
        }

        News saved = newsRepository.save(news);
        return newsMapper.toResponse(saved);
    }


    public NewsResponse update(String id, NewsUpdateRequest request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_NOT_FOUND));

        newsMapper.updateEntityFromDto(request, news);
        return newsMapper.toResponse(newsRepository.save(news));
    }

    public void delete(String id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_NOT_FOUND));
        newsRepository.delete(news);
    }

    public NewsResponse getById(String id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_NOT_FOUND));
        return newsMapper.toResponse(news);
    }

    public Page<NewsResponse> getAll(
            Pageable pageable,
            String keyword,
            String status,
            String category,
            String role
    ) {
        Page<News> page;

        // === 1. ADMIN -> full quyền ===
        if ("ADMIN".equalsIgnoreCase(role)) {
            // 1.1 Nếu có keyword và category
            if (keyword != null && !keyword.isBlank() && category != null && !category.isBlank()) {
                page = newsRepository.findAllByTitleContainingIgnoreCaseAndCategoryIgnoreCase(keyword, category, pageable);
            }
            // 1.2 Nếu chỉ có keyword
            else if (keyword != null && !keyword.isBlank()) {
                page = newsRepository.findAllByTitleContainingIgnoreCase(keyword, pageable);
            }
            // 1.3 Nếu chỉ có category
            else if (category != null && !category.isBlank()) {
                page = newsRepository.findAllByCategoryIgnoreCase(category, pageable);
            }
            // 1.4 Nếu có status
            else if (status != null && !status.isBlank()) {
                try {
                    ContentStatus parsedStatus = ContentStatus.valueOf(status.toUpperCase());
                    page = newsRepository.findAllByStatus(parsedStatus, pageable);
                } catch (IllegalArgumentException e) {
                    throw new AppException(ErrorCode.INVALID_STATUS);
                }
            } else {
                page = newsRepository.findAll(pageable);
            }
        }

        // === 2. USER / RECRUITER -> chỉ thấy PUBLISH ===
        else if ("USER".equalsIgnoreCase(role) || "RECRUITER".equalsIgnoreCase(role)) {
            if (keyword != null && !keyword.isBlank() && category != null && !category.isBlank()) {
                page = newsRepository.findAllByTitleContainingIgnoreCaseAndCategoryIgnoreCaseAndStatus(
                        keyword, category, ContentStatus.PUBLISH, pageable);
            } else if (keyword != null && !keyword.isBlank()) {
                page = newsRepository.findAllByTitleContainingIgnoreCaseAndStatus(
                        keyword, ContentStatus.PUBLISH, pageable);
            } else if (category != null && !category.isBlank()) {
                page = newsRepository.findAllByCategoryIgnoreCaseAndStatus(
                        category, ContentStatus.PUBLISH, pageable);
            } else {
                page = newsRepository.findAllByStatus(ContentStatus.PUBLISH, pageable);
            }
        }

        // === 3. Role khác không hợp lệ ===
        else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return page.map(newsMapper::toResponse);
    }


    @Transactional
    public String uploadImgNews(String newsId, MultipartFile file) {
        log.info(" Upload image for news {} | file={}", newsId, file == null ? "null" : file.getOriginalFilename());

        // 1 Tìm bài viết
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new AppException(ErrorCode.NEWS_NOT_FOUND));

        // 2 Kiểm tra file hợp lệ
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // 3 Upload file mới
        String newUrl = fileStorageService.uploadFile(file, "news");

        // 4 Xoá ảnh cũ (nếu có)
        if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
            fileStorageService.deleteFile(news.getImageUrl());
        }

        // 5 Cập nhật DB
        news.setImageUrl(newUrl);
        newsRepository.save(news);

        log.info(" Updated news {} image -> {}", newsId, newUrl);

        return newUrl;
    }
}
