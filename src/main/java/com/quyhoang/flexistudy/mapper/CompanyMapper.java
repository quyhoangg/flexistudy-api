package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.request.CompanyCreationRequest;
import com.quyhoang.flexistudy.dto.request.CompanyUpdateRequest;
import com.quyhoang.flexistudy.dto.response.CompanyResponse;
import com.quyhoang.flexistudy.entity.Company;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "verificationImageUrl", ignore = true)
    @Mapping(target = "verificationSubmittedAt", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "verificationNote", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    @Mapping(target = "recruiters", ignore = true)
    Company toCompany(CompanyCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "verificationStatus", ignore = true)
    @Mapping(target = "verificationImageUrl", ignore = true)
    @Mapping(target = "verificationSubmittedAt", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "verificationNote", ignore = true)
    void updateCompany(@MappingTarget Company company, CompanyUpdateRequest request);

    CompanyResponse toCompanyResponse(Company company);
}
