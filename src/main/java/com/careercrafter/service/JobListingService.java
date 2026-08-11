package com.careercrafter.service;

import com.careercrafter.dto.request.JobListingReqDto;
import com.careercrafter.dto.response.EmployerListingCountDto;
import com.careercrafter.dto.response.JobListingRespDto;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.Category;
import com.careercrafter.model.Employer;
import com.careercrafter.model.JobListing;
import com.careercrafter.repository.CategoryRepository;
import com.careercrafter.repository.EmployerRepository;
import com.careercrafter.repository.JobListingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobListingService {

    private final JobListingRepository jobListingRepository;
    private final EmployerRepository employerRepository;
    private final CategoryRepository categoryRepository;

    public void insert(String employerUsername, @Valid JobListingReqDto dto) {

        Employer employer = employerRepository.findByUser_Username(employerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Employer invalid.."));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category id invalid.."));

        JobListing jobListing = new JobListing();

        jobListing.setTitle(dto.title());
        jobListing.setDescription(dto.description());
        jobListing.setSalary(dto.salary());
        jobListing.setLocation(dto.location());
        jobListing.setEmployer(employer);
        jobListing.setCategory(category);

        jobListingRepository.save(jobListing);
    }

    public List<JobListingRespDto> getByCategoryId(long categoryId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return jobListingRepository.getByCategoryId(categoryId, pageable);
    }

    public List<EmployerListingCountDto> getListingCountPerEmployer() {

        return jobListingRepository.getListingCountPerEmployer();
    }

    public JobListingRespDto getById(long id) {

        return jobListingRepository.getByIdWithNames(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("JobListing id invalid"));
    }

    public void delete(String username, boolean isAdmin, long id) {

        JobListing jobListing = jobListingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("JobListing id invalid"));

        boolean isOwner = jobListing.getEmployer()
                .getUser()
                .getUsername()
                .equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException(
                    "Not authorized to delete this listing");
        }

        jobListing.setActive(false);
        jobListingRepository.save(jobListing);
    }

    public List<JobListingRespDto> getByEmployerUsername(
            String employerUsername,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return jobListingRepository.getByEmployerUsername(
                employerUsername,
                pageable
        );
    }

    public List<JobListingRespDto> getAllActive(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return jobListingRepository.getAllActive(pageable);
    }

    public List<JobListingRespDto> search(
            String keyword,
            String location,
            Double minSalary,
            Double maxSalary,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return jobListingRepository.search(
                keyword,
                location,
                minSalary,
                maxSalary,
                pageable
        );
    }

    public void update(
            String username,
            boolean isAdmin,
            long id,
            JobListingReqDto dto
    ) {

        JobListing jobListing = jobListingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("JobListing id invalid"));

        boolean isOwner = jobListing.getEmployer()
                .getUser()
                .getUsername()
                .equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException(
                    "Not authorized to edit this listing");
        }

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category id invalid.."));

        jobListing.setTitle(dto.title());
        jobListing.setDescription(dto.description());
        jobListing.setSalary(dto.salary());
        jobListing.setLocation(dto.location());
        jobListing.setCategory(category);

        jobListingRepository.save(jobListing);
    }

    public List<JobListingRespDto> getAllForAdmin(
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return jobListingRepository.getAllForAdmin(pageable);
    }

    public List<JobListingRespDto> searchAdmin(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return jobListingRepository.searchAdmin(
                keyword,
                pageable
        );
    }

    public List<JobListingRespDto> getAdminJobsByCategory(
            long categoryId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return jobListingRepository.getAdminJobsByCategory(
                categoryId,
                pageable
        );
    }
    public long getTotalActiveJobs() {
        return jobListingRepository.countAllActive();
    }

    public long getTotalJobsByCategory(long categoryId) {
        return jobListingRepository.countByCategoryId(categoryId);
    }
}