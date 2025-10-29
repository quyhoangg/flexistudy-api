package com.quyhoang.flexistudy.configuration;

import com.quyhoang.flexistudy.constant.PredefinedRole;
import com.quyhoang.flexistudy.entity.Role;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.repository.RoleRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {

            // 🟢 Seed USER role nếu chưa có
            roleRepository.findByName(RoleName.USER).orElseGet(() ->
                    roleRepository.save(Role.builder()
                            .name(RoleName.USER)
                            .description("User role")
                            .build())
            );

            // 🟢 Seed ADMIN role nếu chưa có
            Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseGet(() ->
                    roleRepository.save(Role.builder()
                            .name(RoleName.ADMIN)
                            .description("Admin role")
                            .build())
            );

            // 🟢 Seed admin user mặc định
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("⚠️ Admin user has been created with default password: admin, please change it");
            }

            log.info(" Application initialization completed.");
        };
    }

}
