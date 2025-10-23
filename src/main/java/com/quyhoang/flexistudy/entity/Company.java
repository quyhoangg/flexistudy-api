package com.quyhoang.flexistudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quyhoang.flexistudy.enums.VerificationStatus;
import com.quyhoang.flexistudy.exception.CompanyVerifiedStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(nullable = false, length = 150, unique = true)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    String logoUrl;

    String website;

    int memberNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    String verificationImageUrl;

    LocalDateTime verificationSubmittedAt;
    LocalDateTime verifiedAt;
    String verificationNote;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    List<Job> jobs = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    List<User> recruiters = new ArrayList<>();
}
