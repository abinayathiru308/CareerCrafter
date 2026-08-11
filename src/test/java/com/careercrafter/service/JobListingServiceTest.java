package com.careercrafter.service;

import com.careercrafter.dto.request.JobListingReqDto;
import com.careercrafter.dto.response.JobListingRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.Category;
import com.careercrafter.model.Employer;
import com.careercrafter.model.JobListing;
import com.careercrafter.model.User;
import com.careercrafter.repository.CategoryRepository;
import com.careercrafter.repository.EmployerRepository;
import com.careercrafter.repository.JobListingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobListingServiceTest {

    @InjectMocks
    private JobListingService jobListingService;

    @Mock
    private JobListingRepository jobListingRepository;
    @Mock
    private EmployerRepository employerRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private User employerUser1;
    private Employer employer1;

    private User employerUser2;
    private Employer employer2;

    private Category category1;

    private JobListing jobListing1;

    @BeforeEach
    public void init(){

        employerUser1 = new User(1L, "employer1", "pass123", Role.EMPLOYER, true);
        employer1 = new Employer(1L, "TechCorp", "Hubli", true, employerUser1);

        employerUser2 = new User(2L, "employer2", "pass123", Role.EMPLOYER, true);
        employer2 = new Employer(2L, "OtherCorp", "Mysore", true, employerUser2);

        category1 = new Category(1L, "IT", 1);

        jobListing1 = new JobListing(1L, "Java Developer", "Backend role", 50000, true, employer1, category1);
    }

    @Test
    public void getByIdTestPresent(){

        JobListingRespDto respDto = new JobListingRespDto(1L, "Java Developer", 50000, "IT", "TechCorp");

        when(jobListingRepository.getByIdWithNames(1L)).thenReturn(Optional.of(respDto));

        Assertions.assertEquals(respDto, jobListingService.getById(1L));

        verify(jobListingRepository, times(1)).getByIdWithNames(1L);
    }

    @Test
    public void getByIdTestNotPresent(){

        when(jobListingRepository.getByIdWithNames(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobListing id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class, () -> {
                    jobListingService.getById(99L);
                }).getMessage());
    }

    @Test
    public void insertTest(){

        JobListingReqDto dto = new JobListingReqDto("Java Developer", "Backend role", 50000, 1L);

        when(employerRepository.findByUser_Username("employer1")).thenReturn(Optional.of(employer1));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        jobListingService.insert("employer1", dto);

        ArgumentCaptor<JobListing> jobListingCaptor = ArgumentCaptor.forClass(JobListing.class);

        verify(jobListingRepository, times(1)).save(jobListingCaptor.capture());

        Assertions.assertEquals(dto.title(), jobListingCaptor.getValue().getTitle());
        Assertions.assertEquals(dto.description(), jobListingCaptor.getValue().getDescription());
        Assertions.assertEquals(dto.salary(), jobListingCaptor.getValue().getSalary());
    }

    @Test
    public void insertTestForInvalidEmployer(){

        JobListingReqDto dto = new JobListingReqDto("Java Developer", "Backend role", 50000, 1L);

        when(employerRepository.findByUser_Username("ghost")).thenReturn(Optional.empty());

        Assertions.assertEquals("Employer invalid..",
                Assertions.assertThrows(ResourceNotFoundException.class, () -> {
                    jobListingService.insert("ghost", dto);
                }).getMessage());

        verify(jobListingRepository, times(0)).save(any(JobListing.class));
    }

    @Test
    public void insertTestForInvalidCategory(){

        JobListingReqDto dto = new JobListingReqDto("Java Developer", "Backend role", 50000, 99L);

        when(employerRepository.findByUser_Username("employer1")).thenReturn(Optional.of(employer1));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("Category id invalid..",
                Assertions.assertThrows(ResourceNotFoundException.class, () -> {
                    jobListingService.insert("employer1", dto);
                }).getMessage());

        verify(jobListingRepository, times(0)).save(any(JobListing.class));
    }

    @Test
    public void updateTest(){

        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        JobListingReqDto dto = new JobListingReqDto("Senior Java Developer", "Updated desc", 70000, 1L);

        jobListingService.update("employer1", false, 1L, dto);

        verify(jobListingRepository, times(1)).findById(1L);
        verify(jobListingRepository, times(1)).save(jobListing1);

        Assertions.assertEquals("Senior Java Developer", jobListing1.getTitle());
        Assertions.assertEquals(70000, jobListing1.getSalary());
    }

    @Test
    public void updateTestForInvalidJobListingId(){

        when(jobListingRepository.findById(99L)).thenReturn(Optional.empty());

        JobListingReqDto dto = new JobListingReqDto("Senior Java Developer", "Updated desc", 70000, 1L);

        Assertions.assertEquals("JobListing id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> jobListingService.update("employer1", false, 99L, dto))
                        .getMessage());

        verify(jobListingRepository, times(1)).findById(99L);
        verify(jobListingRepository, times(0)).save(any(JobListing.class));
    }

    @Test
    public void updateTestForUnauthorizedEmployer(){

        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        JobListingReqDto dto = new JobListingReqDto("Senior Java Developer", "Updated desc", 70000, 1L);

        Assertions.assertEquals("Not authorized to edit this listing",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> jobListingService.update("employer2", false, 1L, dto))
                        .getMessage());

        verify(jobListingRepository, times(0)).save(any(JobListing.class));
    }

    @Test
    public void updateTestAllowedForAdmin(){

        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        JobListingReqDto dto = new JobListingReqDto("Senior Java Developer", "Updated desc", 70000, 1L);

        jobListingService.update("admin1", true, 1L, dto);

        verify(jobListingRepository, times(1)).save(jobListing1);
        Assertions.assertEquals("Senior Java Developer", jobListing1.getTitle());
    }

    @Test
    public void deleteTest(){

        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        jobListingService.delete("employer1", false, 1L);

        verify(jobListingRepository, times(1)).findById(1L);
        verify(jobListingRepository, times(1)).save(jobListing1);

        Assertions.assertEquals(false, jobListing1.isActive());
    }

    @Test
    public void deleteTestForInvalidId(){

        when(jobListingRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobListing id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> jobListingService.delete("employer1", false, 99L))
                        .getMessage());

        verify(jobListingRepository, times(0)).save(any(JobListing.class));
    }

    @Test
    public void deleteTestForUnauthorizedEmployer(){

        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        Assertions.assertEquals("Not authorized to delete this listing",
                Assertions.assertThrows(InvalidCredentialsException.class,
                                () -> jobListingService.delete("employer2", false, 1L))
                        .getMessage());

        verify(jobListingRepository, times(0)).save(any(JobListing.class));
    }

    @Test
    public void deleteTestAllowedForAdmin(){

        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        jobListingService.delete("admin1", true, 1L);

        verify(jobListingRepository, times(1)).save(jobListing1);
        Assertions.assertEquals(false, jobListing1.isActive());
    }
}