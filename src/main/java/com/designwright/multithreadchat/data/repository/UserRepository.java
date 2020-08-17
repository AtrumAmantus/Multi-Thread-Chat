package com.designwright.multithreadchat.data.repository;

import com.designwright.multithreadchat.data.entity.UserEntity;

public interface UserRepository {

    UserEntity getByEmailIgnoreCase(String email);

}
