package com.studyshare.server.repository;

import com.studyshare.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByUsernameContainingIgnoreCase(String username);

    @Modifying
@Query(value = "INSERT INTO users (username, password, email, role) VALUES (:username, :password, :email, :role)", nativeQuery = true)
void insertUser(@Param("username") String username, @Param("password") String password, @Param("email") String email, @Param("role") String role);

}