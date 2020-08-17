package com.designwright.multithreadchat.server2.service;

import com.designwright.multithreadchat.server2.common.util.MappingUtils;
import com.designwright.multithreadchat.server2.data.domain.User;
import com.designwright.multithreadchat.server2.data.entity.UserEntity;
import com.designwright.multithreadchat.server2.data.repository.UserRepository;
import com.designwright.multithreadchat.server2.loader.Service;
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
