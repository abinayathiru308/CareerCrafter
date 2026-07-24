package com.careercrafter.service;
import com.careercrafter.dto.request.JobSeekerDto;
import com.careercrafter.dto.response.JobSeekerRespDto;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.mapper.JobSeekerMapper;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.repository.JobSeekerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class JobSeekerService {
    private final JobSeekerMapper jobSeekerMapper;
    private final JobSeekerRepository jobSeekerRepository;
    public JobSeeker add(JobSeekerDto jobSeekerDto) {
        JobSeeker jobSeeker = jobSeekerMapper.mapDtoToEntity(jobSeekerDto);
        return jobSeekerRepository.save(jobSeeker);
    }
    public List<JobSeekerRespDto> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        List<JobSeeker> list = jobSeekerRepository.fetchAll(pageable).getContent();
        return list
                .stream()
                .map(JobSeekerMapper::mapEntityToDto)
                .toList();
    }
    public JobSeekerRespDto getById(long id) {
        JobSeeker jobSeeker = jobSeekerRepository.fetchById(id)
                .orElseThrow(()-> new ResourceNotFoundException("JobSeeker id Invalid"));
        return JobSeekerMapper.mapEntityToDto(jobSeeker);
    }
    public void delete(long id) {
        JobSeeker jobSeeker = jobSeekerRepository.fetchById(id)
                .orElseThrow(()-> new ResourceNotFoundException("JobSeeker id Invalid"));
        jobSeeker.setActive(false);
        jobSeekerRepository.save(jobSeeker);
    }
    public void update(long id, @Valid JobSeekerDto jobSeekerDto) {
        JobSeeker jobSeekerDB = jobSeekerRepository.fetchById(id)
                .orElseThrow(()-> new ResourceNotFoundException("JobSeeker id Invalid"));
        jobSeekerDB.setName(jobSeekerDto.name());
        jobSeekerDB.setEmail(jobSeekerDto.email());
        jobSeekerDB.setSkills(jobSeekerDto.skills());
        jobSeekerRepository.save(jobSeekerDB);
    }
}