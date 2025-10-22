package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.request.NewsCreateRequest;
import com.quyhoang.flexistudy.dto.request.NewsUpdateRequest;
import com.quyhoang.flexistudy.dto.response.NewsResponse;
import com.quyhoang.flexistudy.entity.News;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    News toEntity(NewsCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(NewsUpdateRequest request, @MappingTarget News news);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", expression = "java(news.getAuthor().getFullName())")
    NewsResponse toResponse(News news);

    List<NewsResponse> toResponseList(List<News> newsList);

    @AfterMapping
    default void mapAuthorInfo(News entity, @MappingTarget NewsResponse response) {
        if (entity.getAuthor() != null) {
            response.setAuthorId(entity.getAuthor().getId());
            response.setAuthorName(entity.getAuthor().getFullName());
        }
    }
}
