package com.designwright.multithreadchat.server2.data.entity;

import lombok.Data;

@Data
public class UserEntity {

    private Long id;
    private String alias;
    private String hashPassword;
    private String email;
    private Long createDate;
    private Long updateDate;
    private Long lastLoginDate;

}
