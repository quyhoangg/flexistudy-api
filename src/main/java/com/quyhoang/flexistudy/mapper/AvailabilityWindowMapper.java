package com.quyhoang.flexistudy.mapper;


import com.quyhoang.flexistudy.dto.response.AvailabilityWindowResponse;
import com.quyhoang.flexistudy.entity.AvailabilityWindow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilityWindowMapper {
    @Mapping(target = "userId", source = "user.id")
    AvailabilityWindowResponse toResponse(AvailabilityWindow entity);
}
