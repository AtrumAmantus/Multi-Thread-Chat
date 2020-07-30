package com.designwright.multithreadchat.server2.data.repository;

import com.designwright.multithreadchat.server2.data.entity.UserEntity;

public interface UserRepository {

    UserEntity getByEmailIgnoreCase(String email);

}
