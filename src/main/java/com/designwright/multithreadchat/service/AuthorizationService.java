package com.designwright.multithreadchat.service;

import com.designwright.multithreadchat.data.entity.UserEntity;
import com.designwright.multithreadchat.data.repository.UserRepository;
import com.designwright.core.container.Component;
import lombok.Data;

@Data
@Component
public class AuthorizationService {

    private final UserRepository userRepository;

    public boolean authorize(String email, String password) {
        UserEntity userEntity = userRepository.getByEmailIgnoreCase(email);

        return userEntity.getHashPassword().equals(password);
    }

}
