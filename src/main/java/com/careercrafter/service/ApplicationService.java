package com.careercrafter.service;

import com.careercrafter.dto.request.ApplicationReqDto;
import com.careercrafter.dto.response.ApplicationRespDto;
import com.careercrafter.exception.DuplicateApplicationException;
import com.careercrafter.exception.InvalidCredentialsException;
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
    private final NotificationService notificationService;

    public void insert(String username, boolean isAdmin, long jobSeekerId, ApplicationReqDto dto) {

        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker id invalid.."));

        boolean isOwner = jobSeeker.getUser().getUsername().equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException("Not authorized to apply on behalf of this jobseeker");
        }

        JobListing jobListing = jobListingRepository.findById(dto.jobListingId())
                .orElseThrow(() -> new ResourceNotFoundException("JobListing id invalid.."));

        boolean alreadyApplied = applicationRepository
                .existsByJobSeekerIdAndJobListingId(jobSeekerId, dto.jobListingId());

        if (alreadyApplied) {
            throw new DuplicateApplicationException("You have already applied to this job");
        }

        Application application = new Application();

        application.setJobSeeker(jobSeeker);
        application.setJobListing(jobListing);
        application.setStatus("APPLIED");
        application.setBatch(dto.batch());
        application.setCourse(dto.course());
        application.setCertifications(dto.certifications());
        application.setCollege(dto.college());
        application.setYearPassedOut(dto.yearPassedOut());
        application.setSkills(dto.skills());

        applicationRepository.save(application);
    }

    public List<ApplicationRespDto> getByJobSeekerEmail(String username, boolean isAdmin, String jobSeekerEmail, int page, int size) {

        JobSeeker jobSeeker = jobSeekerRepository.findByEmail(jobSeekerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker email invalid"));

        boolean isOwner = jobSeeker.getUser().getUsername().equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException("Not authorized to view these applications");
        }

        Pageable pageable = PageRequest.of(page, size);

        return applicationRepository.getByJobSeekerEmail(jobSeekerEmail, pageable);
    }

    public List<ApplicationRespDto> getByJobSeekerId(String username, boolean isAdmin, long jobSeekerId, int page, int size) {

        JobSeeker jobSeeker = jobSeekerRepository.fetchById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker id invalid"));

        boolean isOwner = jobSeeker.getUser().getUsername().equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException("Not authorized to view these applications");
        }

        Pageable pageable = PageRequest.of(page, size);

        return applicationRepository.getByJobSeekerId(jobSeekerId, pageable);
    }

    public void updateStatus(long id, String status) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application id invalid"));

        application.setStatus(status);
        applicationRepository.save(application);

        String username = application.getJobSeeker().getUser().getUsername();
        String message = "Your application for " + application.getJobListing().getTitle()
                + " is now " + status;

        notificationService.sendNotification(username, message);
    }

    public List<ApplicationRespDto> getByJobListingId(String username, boolean isAdmin, long jobListingId, int page, int size) {

        JobListing jobListing = jobListingRepository.findById(jobListingId)
                .orElseThrow(() -> new ResourceNotFoundException("JobListing id invalid"));

        boolean isOwner = jobListing.getEmployer().getUser().getUsername().equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException("Not authorized to view applications for this listing");
        }

        Pageable pageable = PageRequest.of(page, size);

        return applicationRepository.getByJobListingId(jobListingId, pageable);
    }

    public ApplicationRespDto getById(long id) {

        return applicationRepository.getOneById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application id invalid"));
    }

    public void withdraw(String username, boolean isAdmin, long id) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application id invalid"));

        boolean isOwner = application.getJobSeeker().getUser().getUsername().equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException("Not authorized to withdraw this application");
        }

        applicationRepository.deleteById(id);
    }
    public long countByJobListingId(long jobListingId) {
        return applicationRepository.countByJobListingId(jobListingId);
    }
    public List<ApplicationRespDto> getMyApplications(String username, int page, int size) {

        JobSeeker jobSeeker = jobSeekerRepository.fetchByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker not found for user"));

        Pageable pageable = PageRequest.of(page, size);

        return applicationRepository.getByJobSeekerId(jobSeeker.getId(), pageable);
    }

}