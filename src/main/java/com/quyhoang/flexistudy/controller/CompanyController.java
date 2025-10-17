package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.request.CompanyCreationRequest;
import com.quyhoang.flexistudy.dto.request.CompanyUpdateRequest;
import com.quyhoang.flexistudy.dto.response.CompanyResponse;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.service.CompanyService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyController {
    CompanyService companyService;

    @PostMapping
    ApiResponse<CompanyResponse> createCompany(@RequestBody @Valid CompanyCreationRequest request) {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.createCompany(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<CompanyResponse>> getAllCompanies(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search
    ) {
        PageResponse<CompanyResponse> response = companyService.getAllCompanies(page, size, search);
        return ApiResponse.<PageResponse<CompanyResponse>>builder()
                .result(response)
                .build();
    }


    @GetMapping("/{companyId}")
    ApiResponse<CompanyResponse> getCompanyById(@PathVariable String companyId) {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.getCompanyById(companyId))
                .build();
    }

    @PutMapping("/{companyId}")
    ApiResponse<CompanyResponse> updateCompany(@PathVariable String companyId,
                                               @RequestBody CompanyUpdateRequest request) {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.updateCompany(companyId, request))
                .build();
    }

    @DeleteMapping("/{companyId}")
    ApiResponse<String> deleteCompany(@PathVariable String companyId) {
        companyService.deleteCompany(companyId);
        return ApiResponse.<String>builder()
                .result("Company deleted successfully")
                .build();
    }

    @GetMapping("/jobs/{companyId}")
    ApiResponse<List<Job>> getJobsByCompany(@PathVariable String companyId) {
        return ApiResponse.<List<Job>>builder()
                .result(companyService.getJobsByCompany(companyId))
                .build();
    }

    @PostMapping("/{companyId}/recruiters/{userId}")
    ApiResponse<CompanyResponse> assignRecruiter(@PathVariable String companyId, @PathVariable String userId) {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.assignRecruiter(companyId, userId))
                .build();
    }

    @PostMapping("/upload-logo/{companyId}")
    public ApiResponse<String> uploadLogo(
            @PathVariable String companyId,
            @RequestParam("file") MultipartFile file
    ) {
        String fileUrl = companyService.uploadLogo(companyId, file);
        return ApiResponse.<String>builder()
                .result(fileUrl)
                .message("Logo uploaded successfully")
                .build();
    }
}
