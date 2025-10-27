package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.response.ReviewResponse;
import com.quyhoang.flexistudy.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "user.email", target = "userEmail")
    ReviewResponse toResponse(Review r);

    List<ReviewResponse> toResponseList(List<Review> list);
}
