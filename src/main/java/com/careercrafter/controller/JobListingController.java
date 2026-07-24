package com.careercrafter.controller;

import com.careercrafter.dto.request.JobListingReqDto;
import com.careercrafter.dto.response.EmployerListingCountDto;
import com.careercrafter.dto.response.JobListingRespDto;
import com.careercrafter.service.JobListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/joblisting")
@RequiredArgsConstructor
public class JobListingController {

    private final JobListingService jobListingService;

    @PostMapping("/add/{employerId}")
    public void insert(

            @PathVariable long employerId,

            @Valid
            @RequestBody
            JobListingReqDto dto

    ){
        jobListingService.insert(employerId, dto);
    }

    @GetMapping("/by-category/{categoryId}")
    public List<JobListingRespDto> getByCategoryId(

            @PathVariable long categoryId,

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "10") int size

    ){
        return jobListingService.getByCategoryId(categoryId, page, size);
    }

    @GetMapping("/count/per-employer")
    public List<EmployerListingCountDto> getListingCountPerEmployer(){

        return jobListingService.getListingCountPerEmployer();
    }

}