package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.LoginRequest;
import com.amin.jobportal.dto.request.RegisterRequest;
import com.amin.jobportal.dto.request.UpdateUserRequest;
import com.amin.jobportal.dto.response.AuthResponse;
import com.amin.jobportal.dto.response.UserResponse;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.mapper.UserMapper;
import com.amin.jobportal.repository.UserRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if(userRepository.existsUserByEmail(request.getEmail())){
            throw new IllegalArgumentException("Such email registered before");
        }

       User user = mapper.toEntity(request);
       user.setId(null);
       user.setPassword(passwordEncoder.encode(request.getPassword()));

       User dbUser = userRepository.save(user);
       return mapper.toResponse(dbUser);
    }


    @Override
    public UserResponse getCurrentUser() {
        return null;
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = null;
        if(userRepository.findById(id).isPresent()){
            user = userRepository.findById(id).get();
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        User dbUser = userRepository.save(user);
        return userMapper.toResponse(dbUser);
    }

    @Override
    public void deleteAccount() {

    }
}
