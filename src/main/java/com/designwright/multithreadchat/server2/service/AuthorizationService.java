package com.designwright.multithreadchat.server2.service;

import com.designwright.multithreadchat.server2.data.entity.UserEntity;
import com.designwright.multithreadchat.server2.data.repository.UserRepository;
import lombok.Data;

@Data
public class AuthorizationService {

    private final UserRepository userRepository;

    public boolean authorize(String email, String password) {
        UserEntity userEntity = userRepository.getByEmailIgnoreCase(email);

        return userEntity.getHashPassword().equals(password);
    }

}
