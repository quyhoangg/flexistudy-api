package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.News;
import com.quyhoang.flexistudy.enums.ContentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {
    List<News> findAllByStatus(ContentStatus status);
}
