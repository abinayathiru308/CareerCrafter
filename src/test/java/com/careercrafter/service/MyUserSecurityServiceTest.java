package com.careercrafter.service;

import com.careercrafter.enums.Role;
import com.careercrafter.model.User;
import com.careercrafter.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyUserSecurityServiceTest {

    @InjectMocks
    private MyUserSecurityService myUserSecurityService;

    @Mock
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    public void init(){
        user1 = new User(1L, "employer1", "pass123", Role.EMPLOYER, true);
    }

    @Test
    public void loadUserByUsernameTest(){

        when(userRepository.loadUserByUsername("employer1")).thenReturn(Optional.of(user1));

        UserDetails result = myUserSecurityService.loadUserByUsername("employer1");

        Assertions.assertEquals("employer1", result.getUsername());
    }

    @Test
    public void loadUserByUsernameTestNotFound(){

        when(userRepository.loadUserByUsername("ghost")).thenReturn(Optional.empty());

        Assertions.assertEquals("User Credentials Invalid",
                Assertions.assertThrows(UsernameNotFoundException.class,
                                () -> myUserSecurityService.loadUserByUsername("ghost"))
                        .getMessage());
    }
}