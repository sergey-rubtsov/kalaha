package com.game.kalaha.service;

import com.game.kalaha.model.User;
import com.game.kalaha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser() {
        User user = new User();
        return userRepository.save(user);
    }

}
