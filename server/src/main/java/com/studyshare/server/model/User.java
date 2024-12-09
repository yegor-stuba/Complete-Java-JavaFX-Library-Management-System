package com.studyshare.server.model;

import com.studyshare.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_generator")
    @TableGenerator(name = "user_generator", table = "hibernate_sequences",
            pkColumnName = "sequence_name", valueColumnName = "next_val",
            pkColumnValue = "users", initialValue = 1, allocationSize = 1)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
}