package com.amin.jobportal.service;

import com.amin.jobportal.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsServiceImpl implements CustomerUserDetailsService {
    private final UserRepository userRepository;

    public CustomerUserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByEmail(username).
                orElseThrow(() -> new RuntimeException("User with email couldn't find: " + username));
    }
}
