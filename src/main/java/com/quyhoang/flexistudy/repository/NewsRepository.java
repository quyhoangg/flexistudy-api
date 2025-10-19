package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.News;
import com.quyhoang.flexistudy.enums.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {

    Page<News> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<News> findAllByStatus(ContentStatus status, Pageable pageable);

    Page<News> findAllByTitleContainingIgnoreCaseAndStatus(String title, ContentStatus status, Pageable pageable);

    Page<News> findAllByCategoryIgnoreCase(String category, Pageable pageable);

    Page<News> findAllByTitleContainingIgnoreCaseAndCategoryIgnoreCase(String title, String category, Pageable pageable);

    Page<News> findAllByCategoryIgnoreCaseAndStatus(String category, ContentStatus status, Pageable pageable);

    Page<News> findAllByTitleContainingIgnoreCaseAndCategoryIgnoreCaseAndStatus(
            String title, String category, ContentStatus status, Pageable pageable
    );
}

