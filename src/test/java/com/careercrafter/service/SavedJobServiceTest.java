package com.careercrafter.service;

import com.careercrafter.dto.response.SavedJobRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.*;
import com.careercrafter.repository.JobListingRepository;
import com.careercrafter.repository.JobSeekerRepository;
import com.careercrafter.repository.SavedJobRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavedJobServiceTest {

    @InjectMocks
    private SavedJobService savedJobService;

    @Mock
    private SavedJobRepository savedJobRepository;
    @Mock
    private JobSeekerRepository jobSeekerRepository;
    @Mock
    private JobListingRepository jobListingRepository;

    private User jobSeekerUser1;
    private JobSeeker jobSeeker1;

    private User employerUser1;
    private Employer employer1;

    private Category category1;
    private JobListing jobListing1;

    private SavedJob savedJob1;

    @BeforeEach
    public void init(){

        jobSeekerUser1 = new User(1L, "seeker1", "pass123", Role.JOBSEEKER, true);

        // JobSeeker all-args order: id, name, email, phone, skills, resumeUrl, isActive, user, skillSet
        jobSeeker1 = new JobSeeker(1L, "John", "john@gmail.com", "9876543210", "Java", null, true, jobSeekerUser1, new HashSet<>());

        employerUser1 = new User(2L, "employer1", "pass123", Role.EMPLOYER, true);
        employer1 = new Employer(1L, "TechCorp", "Hubli", true, employerUser1);

        category1 = new Category(1L, "IT", 1);

        // JobListing all-args order: id, title, description, salary, isActive, employer, category, location
        jobListing1 = new JobListing(1L, "Java Developer", "Backend role", 50000, true, employer1, category1, "Hubli");

        savedJob1 = new SavedJob(1L, null, jobSeeker1, jobListing1);
    }

    @Test
    public void saveTest(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));
        when(jobListingRepository.findById(1L)).thenReturn(Optional.of(jobListing1));

        savedJobService.save(1L, 1L);

        ArgumentCaptor<SavedJob> captor = ArgumentCaptor.forClass(SavedJob.class);
        verify(savedJobRepository, times(1)).save(captor.capture());

        Assertions.assertEquals(jobSeeker1, captor.getValue().getJobSeeker());
        Assertions.assertEquals(jobListing1, captor.getValue().getJobListing());
    }

    @Test
    public void saveTestForInvalidJobSeeker(){

        when(jobSeekerRepository.fetchById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobSeeker id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> savedJobService.save(99L, 1L))
                        .getMessage());

        verify(savedJobRepository, times(0)).save(any(SavedJob.class));
    }

    @Test
    public void saveTestForInvalidJobListing(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));
        when(jobListingRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobListing id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> savedJobService.save(1L, 99L))
                        .getMessage());

        verify(savedJobRepository, times(0)).save(any(SavedJob.class));
    }

    @Test
    public void getByJobSeekerIdTest(){

        SavedJobRespDto respDto = new SavedJobRespDto(1L, "Java Developer", "TechCorp", null);

        when(savedJobRepository.getByJobSeekerId(1L)).thenReturn(List.of(respDto));

        List<SavedJobRespDto> result = savedJobService.getByJobSeekerId(1L);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void deleteTest(){

        when(savedJobRepository.findById(1L)).thenReturn(Optional.of(savedJob1));

        savedJobService.delete(1L);

        verify(savedJobRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteTestForInvalidId(){

        when(savedJobRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("SavedJob id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> savedJobService.delete(99L))
                        .getMessage());

        verify(savedJobRepository, times(0)).deleteById(anyLong());
    }
}