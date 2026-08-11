package com.careercrafter.service;

import com.careercrafter.dto.request.EmployerReqDto;
import com.careercrafter.dto.response.EmployerRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.Category;
import com.careercrafter.model.Employer;
import com.careercrafter.model.JobListing;
import com.careercrafter.model.User;
import com.careercrafter.repository.EmployerRepository;
import com.careercrafter.repository.JobListingRepository;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployerServiceTest {

    @InjectMocks
    private EmployerService employerService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployerRepository employerRepository;
    @Mock
    private JobListingRepository jobListingRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User employerUser1;
    private Employer employer1;

    private Category category1;
    private JobListing jobListing1;

    @BeforeEach
    public void init(){

        employerUser1 = new User(1L, "employer1", "pass123", Role.EMPLOYER, true);
        employer1 = new Employer(1L, "TechCorp", "Hubli", true, employerUser1);

        category1 = new Category(1L, "IT", 1);

        // JobListing all-args order: id, title, description, salary, isActive, employer, category, location
        jobListing1 = new JobListing(1L, "Java Developer", "Backend role", 50000, true, employer1, category1, "Hubli");
    }

    @Test
    public void getByIdTest(){

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer1));

        EmployerRespDto result = employerService.getById(1L);

        Assertions.assertEquals("TechCorp", result.companyName());
    }

    @Test
    public void getByIdTestNotPresent(){

        when(employerRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("Employer id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> employerService.getById(99L))
                        .getMessage());
    }

    @Test
    public void getAllTest(){

        Pageable pageable = PageRequest.of(0, 10);
        Page<Employer> page = new PageImpl<>(List.of(employer1));

        when(employerRepository.findAll(pageable)).thenReturn(page);

        Assertions.assertEquals(1, employerService.getAll(0, 10).size());

        verify(employerRepository, times(1)).findAll(pageable);
    }

    @Test
    public void deactivateEmployerTest(){

        when(employerRepository.findByUser_Username("employer1")).thenReturn(Optional.of(employer1));
        when(jobListingRepository.findByEmployer_Id(1L)).thenReturn(List.of(jobListing1));

        employerService.deactivateEmployer("employer1", false, "employer1");

        Assertions.assertFalse(employerUser1.isActivated());
        Assertions.assertFalse(jobListing1.isActive());

        verify(employerRepository, times(1)).save(employer1);
        verify(jobListingRepository, times(1)).saveAll(List.of(jobListing1));
    }

    @Test
    public void deactivateEmployerTestForUnauthorizedUser(){

        Assertions.assertEquals("Not authorized to deactivate this employer",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> employerService.deactivateEmployer("intruder", false, "employer1"))
                        .getMessage());

        verify(employerRepository, times(0)).save(any(Employer.class));
    }

    @Test
    public void deactivateEmployerTestAllowedForAdmin(){

        when(employerRepository.findByUser_Username("employer1")).thenReturn(Optional.of(employer1));
        when(jobListingRepository.findByEmployer_Id(1L)).thenReturn(List.of());

        employerService.deactivateEmployer("admin1", true, "employer1");

        verify(employerRepository, times(1)).save(employer1);
    }

    @Test
    public void updateTest(){

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer1));

        EmployerReqDto dto = new EmployerReqDto("NewTechCorp", "Bangalore", "employer1", "pass123");

        employerService.update("employer1", false, 1L, dto);

        Assertions.assertEquals("NewTechCorp", employer1.getCompanyName());
        Assertions.assertEquals("Bangalore", employer1.getCity());

        verify(employerRepository, times(1)).save(employer1);
    }

    @Test
    public void updateTestForUnauthorizedUser(){

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer1));

        EmployerReqDto dto = new EmployerReqDto("NewTechCorp", "Bangalore", "employer1", "pass123");

        Assertions.assertEquals("Not authorized to update this employer",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> employerService.update("intruder", false, 1L, dto))
                        .getMessage());

        verify(employerRepository, times(0)).save(any(Employer.class));
    }

    @Test
    public void insertTest(){

        EmployerReqDto dto = new EmployerReqDto("TechCorp", "Hubli", "employer1", "pass123");

        when(userRepository.existsByUsername("employer1")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(employerUser1);

        employerService.insert(dto);

        ArgumentCaptor<Employer> captor = ArgumentCaptor.forClass(Employer.class);
        verify(employerRepository, times(1)).save(captor.capture());

        Assertions.assertEquals("TechCorp", captor.getValue().getCompanyName());
        Assertions.assertEquals("Hubli", captor.getValue().getCity());
    }

    @Test
    public void insertTestForExistingUsername(){

        EmployerReqDto dto = new EmployerReqDto("TechCorp", "Hubli", "employer1", "pass123");

        when(userRepository.existsByUsername("employer1")).thenReturn(true);

        Assertions.assertEquals("Username already exists",
                Assertions.assertThrows(IllegalArgumentException.class,
                                () -> employerService.insert(dto))
                        .getMessage());

        verify(employerRepository, times(0)).save(any(Employer.class));
    }
}