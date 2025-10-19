package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.request.CompanyCreationRequest;
import com.quyhoang.flexistudy.dto.request.CompanyUpdateRequest;
import com.quyhoang.flexistudy.dto.response.CompanyResponse;
import com.quyhoang.flexistudy.entity.Company;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.CompanyMapper;
import com.quyhoang.flexistudy.repository.CompanyRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;
    CompanyMapper companyMapper;
    UserRepository userRepository;
    FileStorageService fileStorageService;

    public CompanyResponse createCompany(CompanyCreationRequest request) {
        Company company = companyMapper.toCompany(request);
        company = companyRepository.save(company);
        return companyMapper.toCompanyResponse(company);
    }

    public PageResponse<CompanyResponse> getAllCompanies(int page, int size, String search) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Company> companyPage;

        if (search != null && !search.isBlank()) {
            companyPage = companyRepository.search(search, pageable);
        } else {
            companyPage = companyRepository.findAll(pageable);
        }

        List<CompanyResponse> companyResponses = companyPage.getContent()
                .stream()
                .map(companyMapper::toCompanyResponse)
                .toList();

        return PageResponse.<CompanyResponse>builder()
                .currentPage(companyPage.getNumber() + 1)
                .totalPages(companyPage.getTotalPages())
                .pageSize(companyPage.getSize())
                .totalElements(companyPage.getTotalElements())
                .data(companyResponses)
                .build();
    }


    public CompanyResponse getCompanyById(String companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        return companyMapper.toCompanyResponse(company);
    }

    public CompanyResponse updateCompany(String companyId, CompanyUpdateRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        companyMapper.updateCompany(company, request);
        return companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    public void deleteCompany(String companyId) {
        companyRepository.deleteById(companyId);
    }

    public List<Job> getJobsByCompany(String companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        return company.getJobs(); // hoặc jobRepository.findByCompanyId(companyId)
    }

    public CompanyResponse assignRecruiter(String companyId, String userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        User recruiter = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        recruiter.setCompany(company);
        userRepository.save(recruiter);

        return companyMapper.toCompanyResponse(company);
    }

    @Transactional
    public String uploadLogo(String companyId, MultipartFile file) {
        log.info(" Upload logo for company {} | file={}", companyId, file == null ? "null" : file.getOriginalFilename());
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        String newUrl = fileStorageService.uploadFile(file, "logos");

        // Xoá file cũ (nếu có)
        fileStorageService.deleteFile(company.getLogoUrl());

        company.setLogoUrl(newUrl);
        companyRepository.save(company);

        log.info(" Updated company {} logo -> {}", companyId, newUrl);

        return newUrl;
    }
}
