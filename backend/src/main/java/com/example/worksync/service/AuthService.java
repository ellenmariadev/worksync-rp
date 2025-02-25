package com.example.worksync.service;

import com.example.worksync.dto.requests.UserDTO;
import com.example.worksync.exceptions.ConflictException;
import com.example.worksync.model.User;
import com.example.worksync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username);
    }

    public User register(UserDTO userDTO) {
        UserDetails userDetails = this.userRepository.findByEmail(userDTO.getEmail());

        if (userDetails != null) {
            throw new ConflictException("Email already taken");
        }
        String encriptedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());

        User user = new User(userDTO.getEmail(), encriptedPassword, userDTO.getRole(), userDTO.getName());
        return this.userRepository.save(user);
    }

}