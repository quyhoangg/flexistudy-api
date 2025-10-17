package com.quyhoang.flexistudy.entity;

import com.quyhoang.flexistudy.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    RoleName name;

    String description;

    @ManyToMany(fetch = FetchType.EAGER)
    Set<Permission> permissions;
}
