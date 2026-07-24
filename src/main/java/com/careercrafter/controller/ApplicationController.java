package com.careercrafter.controller;

import com.careercrafter.dto.request.ApplicationReqDto;
import com.careercrafter.dto.response.ApplicationRespDto;
import com.careercrafter.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply/{jobSeekerId}")
    public void insert(

            @PathVariable long jobSeekerId,

            @RequestBody
            ApplicationReqDto dto

    ){
        applicationService.insert(jobSeekerId, dto);
    }

    @GetMapping("/by-jobseeker")
    public List<ApplicationRespDto> getByJobSeekerEmail(

            @RequestParam String jobSeekerEmail,

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "5") int size

    ){
        return applicationService.getByJobSeekerEmail(jobSeekerEmail, page, size);
    }

}