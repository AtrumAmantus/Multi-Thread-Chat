package com.designwright.multithreadchat.data.domain;

import com.designwright.core.server.SessionUser;
import lombok.Data;

@Data
public class User implements SessionUser {

    private Long id;
    private String alias;
    private String email;
    private Long createDate;
    private Long updateDate;
    private Long lastLoginDate;

}
