package com.quyhoang.flexistudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quyhoang.flexistudy.enums.EmployeeType;
import com.quyhoang.flexistudy.enums.JobCategory;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.enums.WorkMode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 200)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String requirements;

    @Column(columnDefinition = "TEXT")
    String benefits;

    @Column(length = 255)
    String address;

    LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    EmployeeType type;

    Integer minSalary;
    Integer maxSalary;
    String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    WorkMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    JobCategory category;

    String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    JobStatus status;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    LocalDateTime postedAt = LocalDateTime.now();

    Integer maxApplicants; // Số lượng ứng viên tối đa
    @Builder.Default
    Integer applicantCount = 0; // Số lượng ứng viên hiện tại

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    List<JobShift> jobShifts = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "job_required_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    Set<Skill> requiredSkills = new HashSet<>();

    @ManyToMany(mappedBy = "savedJobs")
    @JsonIgnore
    private Set<User> savedByUsers = new HashSet<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Application> applications = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (status == null) status = JobStatus.CLOSED;
        if (postedAt == null) postedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        Job other = (Job) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
