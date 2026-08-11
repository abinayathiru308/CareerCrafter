package com.careercrafter.service;

import com.careercrafter.dto.request.JobSeekerDto;
import com.careercrafter.dto.response.JobSeekerRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.model.User;
import com.careercrafter.repository.JobSeekerRepository;
import com.careercrafter.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobSeekerServiceTest {

    @InjectMocks
    private JobSeekerService jobSeekerService;

    @Mock
    private JobSeekerRepository jobSeekerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User jobSeekerUser1;
    private JobSeeker jobSeeker1;

    @BeforeEach
    public void init(){

        jobSeekerUser1 = new User(1L, "seeker1", "pass123", Role.JOBSEEKER, true);
        jobSeeker1 = new JobSeeker(1L, "John", "john@gmail.com", "Java", null, true, jobSeekerUser1, new HashSet<>());
    }

    @Test
    public void addTest(){

        JobSeekerDto dto = new JobSeekerDto("John", "john@gmail.com", "Java", "seeker1", "pass123");

        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(jobSeekerUser1);
        when(jobSeekerRepository.save(any(JobSeeker.class))).thenReturn(jobSeeker1);

        jobSeekerService.add(dto);

        ArgumentCaptor<JobSeeker> captor = ArgumentCaptor.forClass(JobSeeker.class);
        verify(jobSeekerRepository, times(1)).save(captor.capture());

        Assertions.assertEquals("John", captor.getValue().getName());
        Assertions.assertEquals("john@gmail.com", captor.getValue().getEmail());
    }

    @Test
    public void getAllTest(){

        Pageable pageable = PageRequest.of(0, 10);
        Page<JobSeeker> page = new PageImpl<>(List.of(jobSeeker1));

        when(jobSeekerRepository.fetchAll(pageable)).thenReturn(page);

        Assertions.assertEquals(1, jobSeekerService.getAll(0, 10).size());

        verify(jobSeekerRepository, times(1)).fetchAll(pageable);
    }

    @Test
    public void getByIdTest(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        JobSeekerRespDto result = jobSeekerService.getById(1L);

        Assertions.assertEquals("John", result.name());
    }

    @Test
    public void getByIdTestNotPresent(){

        when(jobSeekerRepository.fetchById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobSeeker id Invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> jobSeekerService.getById(99L))
                        .getMessage());
    }

    @Test
    public void deleteTest(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        jobSeekerService.delete("seeker1", false, 1L);

        Assertions.assertFalse(jobSeeker1.isActive());
        verify(jobSeekerRepository, times(1)).save(jobSeeker1);
    }

    @Test
    public void deleteTestForUnauthorizedUser(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        Assertions.assertEquals("Not authorized to delete this profile",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> jobSeekerService.delete("intruder", false, 1L))
                        .getMessage());

        verify(jobSeekerRepository, times(0)).save(any(JobSeeker.class));
    }

    @Test
    public void updateTest(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        JobSeekerDto dto = new JobSeekerDto("John Doe", "johnd@gmail.com", "Java, Spring", "seeker1", "pass123");

        jobSeekerService.update("seeker1", false, 1L, dto);

        Assertions.assertEquals("John Doe", jobSeeker1.getName());
        Assertions.assertEquals("johnd@gmail.com", jobSeeker1.getEmail());

        verify(jobSeekerRepository, times(1)).save(jobSeeker1);
    }

    @Test
    public void updateTestForUnauthorizedUser(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        JobSeekerDto dto = new JobSeekerDto("John Doe", "johnd@gmail.com", "Java, Spring", "seeker1", "pass123");

        Assertions.assertEquals("Not authorized to update this profile",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> jobSeekerService.update("intruder", false, 1L, dto))
                        .getMessage());

        verify(jobSeekerRepository, times(0)).save(any(JobSeeker.class));
    }

    @Test
    public void updateResumeTest(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        jobSeekerService.updateResume("seeker1", false, 1L, "http://resume-url.com/john.pdf");

        Assertions.assertEquals("http://resume-url.com/john.pdf", jobSeeker1.getResumeUrl());
        verify(jobSeekerRepository, times(1)).save(jobSeeker1);
    }

    @Test
    public void updateResumeTestForUnauthorizedUser(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        Assertions.assertEquals("Not authorized to update this resume",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> jobSeekerService.updateResume("intruder", false, 1L, "http://x.com"))
                        .getMessage());

        verify(jobSeekerRepository, times(0)).save(any(JobSeeker.class));
    }

    @Test
    public void getResumeTest(){

        jobSeeker1.setResumeUrl("http://resume-url.com/john.pdf");
        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        Assertions.assertEquals("http://resume-url.com/john.pdf", jobSeekerService.getResume(1L));
    }

    @Test
    public void deleteResumeTest(){

        jobSeeker1.setResumeUrl("http://resume-url.com/john.pdf");
        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        jobSeekerService.deleteResume("seeker1", false, 1L);

        Assertions.assertNull(jobSeeker1.getResumeUrl());
        verify(jobSeekerRepository, times(1)).save(jobSeeker1);
    }

    @Test
    public void deleteResumeTestForUnauthorizedUser(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));

        Assertions.assertEquals("Not authorized to delete this resume",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> jobSeekerService.deleteResume("intruder", false, 1L))
                        .getMessage());

        verify(jobSeekerRepository, times(0)).save(any(JobSeeker.class));
    }
}