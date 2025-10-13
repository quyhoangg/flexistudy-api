package com.quyhoang.flexistudy.entity;

import com.quyhoang.flexistudy.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "user_id"})
)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    User user;

    @Column(nullable = false, length = 100)
    String fullName;

    @Column(nullable = false, length = 100)
    String email;

    @Column(nullable = false, length = 20)
    String phone;

    @Column(nullable = false)
    String cvUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Builder.Default
    ApplicationStatus status = ApplicationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false, nullable = false)
    Instant appliedAt;

    @Column(columnDefinition = "TEXT")
    String coverLetter;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "note", length = 2000)
    String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    Company company;

}
