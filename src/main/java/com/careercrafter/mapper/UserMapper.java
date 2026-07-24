package com.careercrafter.mapper;
import com.careercrafter.enums.Role;
import com.careercrafter.model.User;
    public class UserMapper {
        public static User convertDtoToEntity(String username, String password, Role role){
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            return user;
        }
    }