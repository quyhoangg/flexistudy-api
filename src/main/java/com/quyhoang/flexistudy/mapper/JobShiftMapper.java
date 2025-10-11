package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.response.JobShiftResponse;
import com.quyhoang.flexistudy.entity.JobShift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobShiftMapper {
    @Mapping(target = "jobId", source = "job.id")
    JobShiftResponse toResponse(JobShift entity);
}
