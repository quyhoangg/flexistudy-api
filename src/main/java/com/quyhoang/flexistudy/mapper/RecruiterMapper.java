package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.response.CompanyResponse;
import com.quyhoang.flexistudy.dto.response.RecruiterResponse;
import com.quyhoang.flexistudy.entity.Company;
import com.quyhoang.flexistudy.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RecruiterMapper {

    @Mapping(target = "userId", expression = "java(user.getId())")
    @Mapping(target = "email", expression = "java(user.getEmail())")
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "role", expression = "java(\"RECRUITER\")")
    @Mapping(target = "company", source = "company")
    RecruiterResponse toRecruiterResponse(User user, Company company);

    CompanyResponse toCompanyResponse(Company company);
}

