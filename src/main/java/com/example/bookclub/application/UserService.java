package com.example.bookclub.application;

import com.example.bookclub.dto.UserCreateDto;
import com.example.bookclub.dto.UserResultDto;
import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        return userRepository.findById(id).get();
    }

    public UserResultDto createUser(UserCreateDto userCreateDto) {
        User user = userCreateDto.toEntity();
        User createdUser = userRepository.save(user);
        return UserResultDto.of(createdUser);
    }
}
