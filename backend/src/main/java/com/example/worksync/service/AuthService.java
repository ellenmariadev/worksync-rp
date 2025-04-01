package com.example.worksync.service;

import com.example.worksync.dto.requests.UserDTO;
import com.example.worksync.exceptions.ConflictException;
import com.example.worksync.model.User;
import com.example.worksync.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public User register(UserDTO userDTO) {
        if (this.userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ConflictException("Email already taken");
        }

        String encriptedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());

        User user = new User(userDTO.getEmail(), encriptedPassword, userDTO.getRole(), userDTO.getName());
        return this.userRepository.save(user);
    }

    public List<User> listUsers() {
        return this.userRepository.findAll();
    }
}