package com.example.bookclub.application;

import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        return userRepository.findById(id).get();
    }
}
