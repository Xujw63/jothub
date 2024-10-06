package com.jothub.pojo;

import java.time.LocalDateTime;

@lombok.Data
public class User {

    private int uid;

    private String username;

    private String password;

    private String email;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private Boolean isActive = true;

    private Role role = Role.user;

    // 枚举类型：用户角色
    public enum Role {
        admin, user
    }
}

