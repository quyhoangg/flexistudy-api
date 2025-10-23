package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable
    );

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.skills WHERE u.id = :id")
    Optional<User> findByIdWithSkills(@Param("id") String id);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
