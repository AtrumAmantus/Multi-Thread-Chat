package com.designwright.multithreadchat.server2.data.domain;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String alias;
    private String email;
    private Long createDate;
    private Long updateDate;
    private Long lastLoginDate;

}
