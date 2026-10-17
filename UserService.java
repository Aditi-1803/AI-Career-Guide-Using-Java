package com.careeriq.service;

import com.careeriq.model.User;
import com.careeriq.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(User user) {

        String email = user.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }

        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public Optional<User> authenticate(
            String email, String rawPassword) {

        Optional<User> result =
                userRepository.findByEmail(email.trim().toLowerCase());

        if (result.isPresent()
                && passwordEncoder.matches(
                rawPassword, result.get().getPassword())) {
            return result;
        }

        return Optional.empty();
    }
}
