package com.careercrafter.service;

import com.careercrafter.dto.request.SkillReqDto;
import com.careercrafter.dto.response.SkillRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.model.Skill;
import com.careercrafter.model.User;
import com.careercrafter.repository.JobSeekerRepository;
import com.careercrafter.repository.SkillRepository;
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
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private JobSeekerRepository jobSeekerRepository;

    private User jobSeekerUser1;
    private JobSeeker jobSeeker1;
    private Skill skill1;

    @BeforeEach
    public void init(){

        jobSeekerUser1 = new User(1L, "seeker1", "pass123", Role.JOBSEEKER, true);
        jobSeeker1 = new JobSeeker(1L, "John", "john@gmail.com", "Java", null, true, jobSeekerUser1, new HashSet<>());

        skill1 = new Skill(1L, "Java");
    }

    @Test
    public void insertTest(){

        SkillReqDto dto = new SkillReqDto("Java");

        skillService.insert(dto);

        ArgumentCaptor<Skill> captor = ArgumentCaptor.forClass(Skill.class);
        verify(skillRepository, times(1)).save(captor.capture());

        Assertions.assertEquals("Java", captor.getValue().getName());
    }

    @Test
    public void getAllTest(){

        when(skillRepository.findAll()).thenReturn(List.of(skill1));

        List<SkillRespDto> result = skillService.getAll();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Java", result.get(0).name());
    }

    @Test
    public void addSkillToJobSeekerTest(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));

        skillService.addSkillToJobSeeker(1L, 1L);

        Assertions.assertTrue(jobSeeker1.getSkillSet().contains(skill1));
        verify(jobSeekerRepository, times(1)).save(jobSeeker1);
    }

    @Test
    public void addSkillToJobSeekerTestForInvalidJobSeeker(){

        when(jobSeekerRepository.fetchById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("JobSeeker id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> skillService.addSkillToJobSeeker(99L, 1L))
                        .getMessage());

        verify(jobSeekerRepository, times(0)).save(any(JobSeeker.class));
    }

    @Test
    public void addSkillToJobSeekerTestForInvalidSkill(){

        when(jobSeekerRepository.fetchById(1L)).thenReturn(Optional.of(jobSeeker1));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("Skill id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> skillService.addSkillToJobSeeker(1L, 99L))
                        .getMessage());

        verify(jobSeekerRepository, times(0)).save(any(JobSeeker.class));
    }
}