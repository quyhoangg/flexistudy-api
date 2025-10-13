package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.request.EducationRequest;
import com.quyhoang.flexistudy.dto.request.ExperienceRequest;
import com.quyhoang.flexistudy.dto.response.EducationResponse;
import com.quyhoang.flexistudy.dto.response.ExperienceResponse;
import com.quyhoang.flexistudy.entity.Education;
import com.quyhoang.flexistudy.entity.Experience;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Education toEducationEntity(EducationRequest request);
    EducationResponse toEducationResponse(Education entity);

    Experience toExperienceEntity(ExperienceRequest request);
    ExperienceResponse toExperienceResponse(Experience entity);
}

