package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.response.ApplicationResponse;
import com.quyhoang.flexistudy.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(mapFullName(entity))")
    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "jobLogoUrl", source = "job.company.logoUrl")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "city", source = "job.city")
    @Mapping(target = "postedAt", source = "job.postedAt")
    @Mapping(target = "type", source = "job.type")
    @Mapping(target = "minSalary", source = "job.minSalary")
    @Mapping(target = "maxSalary", source = "job.maxSalary")
    ApplicationResponse toResponse(Application entity);

    default String mapFullName(Application entity) {
        if (entity.getUser() == null) return null;
        String first = entity.getUser().getFirstName() != null ? entity.getUser().getFirstName() : "";
        String last = entity.getUser().getLastName() != null ? entity.getUser().getLastName() : "";
        return (first + " " + last).trim();
    }
}
