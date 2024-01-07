package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterDTO;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(String userName, String password) {
        Optional<ru.skypro.homework.model.User> userOptional = userRepository.findByEmail(userName);
        return userOptional.filter(user -> encoder.matches(password, user.getPasswordHash())).isPresent();
    }

    @Override
    public boolean register(RegisterDTO register) {
        if (userRepository.findByEmail(register.getUsername()).isPresent()) {
            return false;
        }
        ru.skypro.homework.model.User user = new ru.skypro.homework.model.User();
        user.setEmail(register.getUsername());
        user.setPhone(register.getPhone());
        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setRole(register.getRole());
        user.setPasswordHash(encoder.encode(register.getPassword()));
        userRepository.save(user);
        return true;
    }

}
