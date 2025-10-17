package com.quyhoang.flexistudy.dto;

import com.quyhoang.flexistudy.enums.EmployeeType;
import lombok.Builder;
import lombok.Data;

// JobQuery.java
@Data
@Builder
public class JobQuery {
    private int page;        // 1-based
    private int size;
    private String search;
    private String city;
    private Boolean urgent;          // null = không lọc
    private EmployeeType type;       // FULLTIME/PARTTIME/INTERN
    private Integer minSalary;
    private Integer maxSalary;
    private Integer recentDays;      // null = không lọc theo thời gian
}

