package com.careercrafter.service;

import com.careercrafter.dto.request.ApplicationReqDto;
import com.careercrafter.dto.response.ApplicationRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.*;
        import com.careercrafter.repository.ApplicationRepository;
import com.careercrafter.repository.JobListingRepository;
import com.careercrafter.repository.JobSeekerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @InjectMocks
    private ApplicationService applicationService;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private JobSeekerRepository jobSeekerRepository;
    @Mock
    private JobListingRepository jobListingRepository;
    @Mock
    private NotificationService notificationService;

    private User jobSeekerUser;
    private JobSeeker jobSeeker1;

    private User employerUser;
    private Employer employer1;

    private Category category1;
    private JobListing jobListing1;

    private Application application1;

    @BeforeEach
    public void init(){

        jobSeekerUser = new User(1L, "seeker1", "pass123", Role.JOBSEEKER, true);
        jobSeeker1 = new JobSeeker(1L, "John", "john@gmail.com", "Java", null, true, jobSeekerUser, new java.util.HashSet<>());

        employerUser = new User(2L, "employer1", "pass123", Role.EMPLOYER, true);
        employer1 = new Employer(1L, "TechCorp", "Hubli", true, employerUser);

        category1 = new Category(1L, "IT", 1);
        jobListing1 = new JobListing(1L, "Java Developer", "Backend role", 50000, true, employer1, category1);

        application1 = new Application(1L, "APPLIED", jobListing1, jobSeeker1);
    }

    @Test
    public void insertTest(){

        ApplicationReqDto dto = new ApplicationReqDto(1L);

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(jobSeeker1));
        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        applicationService.insert("seeker1", false, 1L, dto);

        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository, times(1)).save(captor.capture());

        Assertions.assertEquals("APPLIED", captor.getValue().getStatus());
        Assertions.assertEquals(jobSeeker1, captor.getValue().getJobSeeker());
        Assertions.assertEquals(jobListing1, captor.getValue().getJobListing());
    }

    @Test
    public void insertTestForInvalidJobSeeker(){

        ApplicationReqDto dto = new ApplicationReqDto(1L);

        when(jobSeekerRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobSeeker id invalid..",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> applicationService.insert("seeker1", false, 99L, dto))
                        .getMessage());

        verify(applicationRepository, times(0)).save(any(Application.class));
    }

    @Test
    public void insertTestForUnauthorizedUser(){

        ApplicationReqDto dto = new ApplicationReqDto(1L);

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(jobSeeker1));

        Assertions.assertEquals("Not authorized to apply on behalf of this jobseeker",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> applicationService.insert("intruder", false, 1L, dto))
                        .getMessage());

        verify(applicationRepository, times(0)).save(any(Application.class));
    }

    @Test
    public void insertTestForInvalidJobListing(){

        ApplicationReqDto dto = new ApplicationReqDto(99L);

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(jobSeeker1));
        when(jobListingRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobListing id invalid..",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> applicationService.insert("seeker1", false, 1L, dto))
                        .getMessage());

        verify(applicationRepository, times(0)).save(any(Application.class));
    }

    @Test
    public void getByJobSeekerEmailTest(){

        Assertions.assertNotNull(jobSeeker1.getEmail());
        when(jobSeekerRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(jobSeeker1));

        List<ApplicationRespDto> list = List.of();
        when(applicationRepository.getByJobSeekerEmail(eq("john@gmail.com"), any())).thenReturn(list);

        Assertions.assertEquals(list, applicationService.getByJobSeekerEmail("seeker1", false, "john@gmail.com", 0, 5));

        verify(applicationRepository, times(1)).getByJobSeekerEmail(eq("john@gmail.com"), any());
    }

    @Test
    public void getByJobSeekerEmailTestForInvalidEmail(){

        when(jobSeekerRepository.findByEmail("ghost@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertEquals("JobSeeker email invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> applicationService.getByJobSeekerEmail("seeker1", false, "ghost@gmail.com", 0, 5))
                        .getMessage());
    }

    @Test
    public void getByJobSeekerEmailTestForUnauthorizedUser(){

        when(jobSeekerRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(jobSeeker1));

        Assertions.assertEquals("Not authorized to view these applications",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> applicationService.getByJobSeekerEmail("intruder", false, "john@gmail.com", 0, 5))
                        .getMessage());
    }

    @Test
    public void updateStatusTest(){

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application1));

        applicationService.updateStatus(1L, "SHORTLISTED");

        verify(applicationRepository, times(1)).save(application1);
        verify(notificationService, times(1)).sendNotification(eq("seeker1"), anyString());

        Assertions.assertEquals("SHORTLISTED", application1.getStatus());
    }

    @Test
    public void updateStatusTestForInvalidId(){

        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("Application id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> applicationService.updateStatus(99L, "SHORTLISTED"))
                        .getMessage());

        verify(notificationService, times(0)).sendNotification(anyString(), anyString());
    }

    @Test
    public void getByIdTest(){

        ApplicationRespDto respDto = new ApplicationRespDto(1L, "APPLIED", "Java Developer", "TechCorp", "John");

        when(applicationRepository.getOneById(1L)).thenReturn(Optional.of(respDto));

        Assertions.assertEquals(respDto, applicationService.getById(1L));
    }

    @Test
    public void getByIdTestForInvalidId(){

        when(applicationRepository.getOneById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("Application id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> applicationService.getById(99L))
                        .getMessage());
    }

    @Test
    public void withdrawTest(){

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application1));

        applicationService.withdraw("seeker1", false, 1L);

        verify(applicationRepository, times(1)).deleteById(1L);
    }

    @Test
    public void withdrawTestForInvalidId(){

        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("Application id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> applicationService.withdraw("seeker1", false, 99L))
                        .getMessage());

        verify(applicationRepository, times(0)).deleteById(anyLong());
    }

    @Test
    public void withdrawTestForUnauthorizedUser(){

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application1));

        Assertions.assertEquals("Not authorized to withdraw this application",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> applicationService.withdraw("intruder", false, 1L))
                        .getMessage());

        verify(applicationRepository, times(0)).deleteById(anyLong());
    }
}