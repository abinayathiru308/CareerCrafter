package com.careercrafter.service;

import com.careercrafter.dto.response.SavedJobRespDto;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.JobListing;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.model.SavedJob;
import com.careercrafter.repository.JobListingRepository;
import com.careercrafter.repository.JobSeekerRepository;
import com.careercrafter.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JobListingRepository jobListingRepository;

    public void save(long jobSeekerId, long jobListingId) {

        JobSeeker jobSeeker = jobSeekerRepository.fetchById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker id invalid"));

        JobListing jobListing = jobListingRepository.findById(jobListingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing id invalid"));

        SavedJob savedJob = new SavedJob();

        savedJob.setJobSeeker(jobSeeker);
        savedJob.setJobListing(jobListing);

        savedJobRepository.save(savedJob);
    }

    public List<SavedJobRespDto> getByJobSeekerId(long jobSeekerId) {

        return savedJobRepository.getByJobSeekerId(jobSeekerId);
    }

    public void delete(long id) {

        SavedJob savedJob = savedJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SavedJob id invalid"));

        savedJobRepository.deleteById(savedJob.getId());
    }

}