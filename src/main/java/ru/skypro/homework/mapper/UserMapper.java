package ru.skypro.homework.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.User;
@Component
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getUserId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());

        return userDTO;
    }


    public  UpdateUserDTO toUpdateUserDTO(User user) {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();

        updateUserDTO.setFirstName(user.getFirstName());
        updateUserDTO.setLastName(user.getLastName());
        updateUserDTO.setPhone(user.getPhone());

        return updateUserDTO;
    }

    public  User updateUserFromDTO(User user, UpdateUserDTO updateUserDTO) {
        user.setFirstName(updateUserDTO.getFirstName());
        user.setLastName(updateUserDTO.getLastName());
        user.setPhone(updateUserDTO.getPhone());
        
        return user;
    }
}
