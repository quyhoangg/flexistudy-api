package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.enums.EmployeeType;
import com.quyhoang.flexistudy.enums.JobStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class JobSpecs {
    private JobSpecs() {}

    public static Specification<Job> statusOpen() {
        return (root, q, cb) -> cb.equal(root.get("status"), JobStatus.OPEN);
    }

    public static Specification<Job> postedSince(LocalDateTime cutoff) {
        if (cutoff == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("postedAt"), cutoff);
    }

    public static Specification<Job> cityEquals(String city) {
        if (city == null || city.trim().isEmpty()) return null;
        String v = city.trim().toLowerCase();
        return (root, q, cb) -> cb.equal(cb.lower(root.get("city")), v);
    }

    public static Specification<Job> typeEquals(EmployeeType type) {
        if (type == null) return null;
        return (root, q, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Job> matchesSearch(String search) {
        if (search == null || search.trim().isEmpty()) return null;
        String like = "%" + search.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like),
                cb.like(cb.lower(root.get("requirements")), like),
                cb.like(root.get("company").get("name"), "%" + search + "%")
        );
    }

    /** Lọc lương theo quy tắc "overlap": dải [jobMin, jobMax] giao với [min,max] */
    public static Specification<Job> salaryBetween(Integer min, Integer max) {
        if (min == null && max == null) return null;
        return (root, q, cb) -> {
            var minExpr = cb.coalesce(root.get("minSalary"), cb.literal(0));
            var maxExpr = cb.coalesce(root.get("maxSalary"), cb.literal(Integer.MAX_VALUE));
            if (min != null && max != null) {
                return cb.and(
                        cb.lessThanOrEqualTo(minExpr, max),
                        cb.greaterThanOrEqualTo(maxExpr, min)
                );
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(maxExpr, min);
            } else {
                return cb.lessThanOrEqualTo(minExpr, max);
            }
        };
    }
}