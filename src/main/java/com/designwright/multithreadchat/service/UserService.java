package com.designwright.multithreadchat.service;

import com.designwright.common.util.MappingUtils;
import com.designwright.multithreadchat.data.domain.User;
import com.designwright.multithreadchat.data.entity.UserEntity;
import com.designwright.multithreadchat.data.repository.UserRepository;
import com.designwright.core.container.Service;
import lombok.Data;

@Data
@Service
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        UserEntity userEntity = userRepository.getByEmailIgnoreCase(email);

        return MappingUtils.convertToType(userEntity, User.class);
    }

}
