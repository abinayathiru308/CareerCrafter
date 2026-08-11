package com.careercrafter.service;

import com.careercrafter.dto.request.SkillReqDto;
import com.careercrafter.dto.response.SkillRespDto;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.mapper.SkillMapper;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.model.Skill;
import com.careercrafter.repository.JobSeekerRepository;
import com.careercrafter.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public void insert(SkillReqDto dto) {

        Skill skill = SkillMapper.convertDtoToEntity(dto);

        skillRepository.save(skill);
    }

    public List<SkillRespDto> getAll() {

        List<Skill> list = skillRepository.findAll();

        return list
                .stream()
                .map(SkillMapper::convertEntityToDto)
                .toList();
    }

    public void addSkillToJobSeeker(long jobSeekerId, long skillId) {

        JobSeeker jobSeeker = jobSeekerRepository.fetchById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker id invalid"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill id invalid"));

        jobSeeker.getSkillSet().add(skill);

        jobSeekerRepository.save(jobSeeker);
    }

}