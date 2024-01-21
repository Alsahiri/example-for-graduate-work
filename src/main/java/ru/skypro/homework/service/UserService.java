package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.User;

public interface UserService {
    void setNewPassword(Authentication authentication, NewPasswordDTO newPasswordDTO);

    UserDTO getUserInfoByUsername(Authentication authentication);

    User getUserByUsername(String username);

    User getUserById(Integer userId);

    User getCurrentUser(Authentication authentication);

    UpdateUserDTO updateUserInfo(Authentication authentication, UpdateUserDTO updateUserDTO);

    User loadUserByEmail(String email) throws UsernameNotFoundException;

    boolean userExists(String email);

    void createUser(User user);
}
