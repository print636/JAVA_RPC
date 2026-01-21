package com.kama.provider.impl;

import com.kama.pojo.User;
import com.kama.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;

/**
 * User Service Implementation for RPC demonstration
 * @Version 1.0.0
 */
@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    public User getUserByUserId(Integer id) {
        log.info("客户端查询了ID={}的用�?, id);
        // Simulate database query behavior
        Random random = new Random();
        User user = User.builder()
                .userName(UUID.randomUUID().toString())  // Generate random username
                .id(id)
                .gender(random.nextBoolean())  // Generate random gender
                .build();
        log.info("Returning user info: {}", user);
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        log.info("Data inserted successfully, username={}", user.getUserName());
        // Simulate returning the user ID after insertion
        return user.getId();
    }
}

