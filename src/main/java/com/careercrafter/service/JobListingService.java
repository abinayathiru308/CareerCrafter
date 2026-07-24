package com.careercrafter.service;

import com.careercrafter.dto.request.JobListingReqDto;
import com.careercrafter.dto.response.EmployerListingCountDto;
import com.careercrafter.dto.response.JobListingRespDto;
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

    public void insert(long employerId, @Valid JobListingReqDto dto) {

        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer id invalid.."));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category id invalid.."));

        JobListing jobListing = new JobListing();

        jobListing.setTitle(dto.title());
        jobListing.setDescription(dto.description());
        jobListing.setSalary(dto.salary());
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

}