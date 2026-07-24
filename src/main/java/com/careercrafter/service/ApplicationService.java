package com.careercrafter.service;

import com.careercrafter.dto.request.ApplicationReqDto;
import com.careercrafter.dto.response.ApplicationRespDto;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.Application;
import com.careercrafter.model.JobListing;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.repository.ApplicationRepository;
import com.careercrafter.repository.JobListingRepository;
import com.careercrafter.repository.JobSeekerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JobListingRepository jobListingRepository;

    public void insert(long jobSeekerId, ApplicationReqDto dto) {

        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker id invalid.."));

        JobListing jobListing = jobListingRepository.findById(dto.jobListingId())
                .orElseThrow(() -> new ResourceNotFoundException("JobListing id invalid.."));

        Application application = new Application();

        application.setJobSeeker(jobSeeker);
        application.setJobListing(jobListing);
        application.setStatus("APPLIED");

        applicationRepository.save(application);
    }

    public List<ApplicationRespDto> getByJobSeekerEmail(String jobSeekerEmail, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return applicationRepository.getByJobSeekerEmail(jobSeekerEmail, pageable);
    }

}