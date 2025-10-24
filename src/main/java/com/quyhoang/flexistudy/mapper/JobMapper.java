package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.request.JobCreationRequest;
import com.quyhoang.flexistudy.dto.request.JobUpdateRequest;
import com.quyhoang.flexistudy.dto.response.JobResponse;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.Skill;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JobMapper {

    Job toJob(JobCreationRequest req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJob(@MappingTarget Job job, JobUpdateRequest req);

    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyLogoUrl", source = "company.logoUrl")
    @Mapping(target = "requiredSkills", source = "requiredSkills")
    @Mapping(target = "maxApplicants", source = "maxApplicants")
    @Mapping(target = "applicantCount", source = "applicantCount")
    JobResponse toJobResponse(Job job);

    @AfterMapping
    default void ensureListsNotNull(@MappingTarget JobResponse out) {
        if (out.getRequiredSkills() == null) {
            out.setRequiredSkills(new ArrayList<>());
        }
    }
}

