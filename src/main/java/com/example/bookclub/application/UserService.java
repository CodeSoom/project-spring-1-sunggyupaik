package com.example.bookclub.application;

import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.UserCreateDto;
import com.example.bookclub.dto.UserResultDto;
import com.example.bookclub.dto.UserUpdateDto;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.errors.UserEmailDuplicatedException;
import com.example.bookclub.errors.UserNicknameDuplicatedException;
import com.example.bookclub.errors.UserNotFoundException;
import com.example.bookclub.errors.UserPasswordBadRequestException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final EmailAuthenticationRepository emailAuthenticationRepository;

    public UserService(UserRepository userRepository,
                       EmailAuthenticationRepository emailAuthenticationRepository
    ) {
        this.userRepository = userRepository;
        this.emailAuthenticationRepository = emailAuthenticationRepository;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserResultDto createUser(UserCreateDto userCreateDto) {
        String email = userCreateDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicatedException(email);
        }

        EmailAuthentication emailAuthentication = getAuthenticationNumber(email);
        String authenticationNumber = emailAuthentication.getAuthenticationNumber();
        if (!emailAuthentication.isSameWith(authenticationNumber)) {
            throw new EmailNotAuthenticatedException(authenticationNumber);
        }

        String nickname = userCreateDto.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            throw new UserNicknameDuplicatedException(nickname);
        }

        User user = userCreateDto.toEntity();
        User createdUser = userRepository.save(user);
        deleteEmailAuthentication(emailAuthentication.getEmail());

        return UserResultDto.of(createdUser);
    }

    public UserResultDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = getUser(id);

        String password = userUpdateDto.getPassword();
        if (!user.isPasswordSameWith(password)) {
            throw new UserPasswordBadRequestException();
        }

        String nickname = userUpdateDto.getNickname();
        if (isNicknameDuplicated(id, nickname)) {
            throw new UserNicknameDuplicatedException(nickname);
        }

        String profileImage = userUpdateDto.getProfileImage();
        user.updateWith(nickname, password, profileImage);

        return UserResultDto.of(user);
    }

    public EmailAuthentication getAuthenticationNumber(String email) {
        return emailAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotAuthenticatedException(email));
    }

    public void deleteEmailAuthentication(String email) {
        EmailAuthentication emailAuthentication = getAuthenticationNumber(email);
        emailAuthenticationRepository.delete(emailAuthentication);
    }

    public UserResultDto deleteUser(Long id) {
        User user = getUser(id);

        user.delete();

        return UserResultDto.of(user);
    }

    public boolean isNicknameDuplicated(Long id, String nickname) {
        return userRepository.findByIdNotAndNickname(id, nickname);
    }
}
