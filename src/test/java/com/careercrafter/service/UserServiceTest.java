package com.careercrafter.service;

import com.careercrafter.dto.request.AdminReqDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.model.User;
import com.careercrafter.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User adminUser1;

    @BeforeEach
    public void init(){
        adminUser1 = new User(1L, "admin1", "encodedPass", Role.ADMIN, true);
    }

    @Test
    public void addAdminTest(){

        AdminReqDto dto = new AdminReqDto("admin1", "pass123");

        when(userRepository.existsByUsername("admin1")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        userService.addAdmin(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        Assertions.assertEquals("admin1", captor.getValue().getUsername());
        Assertions.assertEquals(Role.ADMIN, captor.getValue().getRole());
    }

    @Test
    public void addAdminTestForExistingUsername(){

        AdminReqDto dto = new AdminReqDto("admin1", "pass123");

        when(userRepository.existsByUsername("admin1")).thenReturn(true);

        Assertions.assertEquals("Username already exists",
                Assertions.assertThrows(IllegalArgumentException.class,
                                () -> userService.addAdmin(dto))
                        .getMessage());

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void getUserDetailsTest(){

        when(userRepository.loadUserByUsername("admin1")).thenReturn(Optional.of(adminUser1));

        Assertions.assertEquals(adminUser1, userService.getUserDetails("admin1"));
    }

    @Test
    public void getUserDetailsTestForInvalidUser(){

        when(userRepository.loadUserByUsername("ghost")).thenReturn(Optional.empty());

        Assertions.assertEquals("Login Denied",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> userService.getUserDetails("ghost"))
                        .getMessage());
    }
}