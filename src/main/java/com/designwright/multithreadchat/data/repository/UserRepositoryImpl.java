package com.designwright.multithreadchat.data.repository;

import com.designwright.multithreadchat.exception.ResourceNotFoundException;
import com.designwright.multithreadchat.data.entity.UserEntity;
import com.designwright.core.container.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final List<UserEntity> users;

    public UserRepositoryImpl() {
        users = new ArrayList<>();
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setAlias("Atrum");
        user1.setEmail("atrum@test.com");
        user1.setHashPassword("hash");
        user1.setCreateDate(0L);
        user1.setUpdateDate(1L);
        user1.setLastLoginDate(1L);

        users.add(user1);
    }

    @Override
    public UserEntity getByEmailIgnoreCase(String email) {
        Optional<UserEntity> optional = users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new ResourceNotFoundException("User with given email not found");
    }

}
