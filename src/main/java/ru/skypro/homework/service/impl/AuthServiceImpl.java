package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterDTO;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService, UserMapper userMapper) {
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Производит проверку логина/пароля пользователя на соответствие данным, хранящимся в БД
     *
     * @param email    уникальное имя, логин пользователя (его email), предоставляет фронтенд
     * @param password пароль пользователя, предоставляет фронтенд
     * @return true - если пароль, переданный в параметре password соответствует закодированному паролю, хранящемуся в БД
     * @throws UsernameNotFoundException - если пользователь с указанным userName не найден в БД
     */
    @Override
    public boolean login(String email, String password) {
        Optional<ru.skypro.homework.model.User> userOptional = userRepository.findByEmail(email);
        return userOptional.filter(user -> encoder.matches(password, user.getPasswordHash())).isPresent();
    }

    /**
     * Создает нового пользователя в соответствии с переданными параметрами и сохраняет его в БД.
     * Если пользователь с указанным логином уже существует, то создание не происходит, возвращает false
     *
     * @param registerDTO - объект класса RegisterDTO, содержащий регистрационную информацию о новом пользователе,
     *                    предоставляет фронтенд
     * @return true - если пользователь был успешно зарегистрирован и сохранен в БД
     */
    @Override
    public boolean register(RegisterDTO registerDTO) {
        if (userService.userExists(registerDTO.getUsername())) {
            return false;
        }
        User user = userMapper.fromRegisterDTO(registerDTO);
        userService.createUser(user);
        return true;

    }
}
