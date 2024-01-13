package ru.skypro.homework.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * Производит смену пароля для текущего авторизованного пользователя и сохраняет его в БД
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     * @param newPasswordDTO объект содержащий текущий и новый парль для пользователя
     */
    @Override
    public void setNewPassword(Authentication authentication, NewPasswordDTO newPasswordDTO) {
        User user = getCurrentUser(authentication);
        if (encoder.matches(newPasswordDTO.getCurrentPassword(), user.getPasswordHash())) {
            user.setPasswordHash(encoder.encode(newPasswordDTO.getNewPassword()));
            userRepository.save(user);
        }
    }

    /**
     * Возвращает информационую запись (UserDTO) о текущем авторизованном пользователе
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     * @return Возвращает объект класса UserDTO
     */
    @Override
    public UserDTO getUserInfoByUsername(Authentication authentication) {
        User user = getCurrentUser(authentication);
        ;
        return userMapper.toUserDTO(user);
    }

    /**
     * Производит поиск в БД пользователя по его уникальному имени (логину)
     * @param username Логин пользователя, используемый при регистрации и входе в систему
     * @return Пользователь в виде объекта класса AdUser
     * @throws UsernameNotFoundException - если пользователь не найден в БД по указанному имени
     */
    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с логином: %s не найден в БД", username));
        }
        return user;
    }

    /**
     * Производит поиск в БД пользователя по его уникальному идентификатору id
     * @param userId идентификатор пользователя
     * @return Пользователь в виде объекта класса AdUser
     * @throws UserNotFoundException если пользователь не найден в БД по указанному идентификатору
     */
    @Override
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id: %d не найден в БД", userId)));
    }

    /**
     * Возвращает объект класса AdUser, соответствующий текущему авторизованному пользователю
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     * @return Пользователь в виде объекта класса AdUser
     */
    @Override
    public User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return getUserByUsername(username);
    }

    @Override
    public UpdateUserDTO updateUserInfo(Authentication authentication, UpdateUserDTO updateUserDTO) {
        User user = getCurrentUser(authentication);
        User updatedUser = userMapper.updateUserFromDTO(user, updateUserDTO);
        updatedUser = userRepository.save(updatedUser);
        return userMapper.toUpdateUserDTO(updatedUser);
    }
}
