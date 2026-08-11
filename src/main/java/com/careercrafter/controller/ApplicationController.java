package com.careercrafter.controller;

import com.careercrafter.dto.request.ApplicationReqDto;
import com.careercrafter.dto.response.ApplicationRespDto;
import com.careercrafter.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply/{jobSeekerId}")
    public void insert(

            Principal principal,

            @PathVariable long jobSeekerId,

            @RequestBody
            ApplicationReqDto dto

    ){
        boolean isAdmin = isAdmin(principal);
        applicationService.insert(principal.getName(), isAdmin, jobSeekerId, dto);
    }

    @GetMapping("/by-jobseeker")
    public List<ApplicationRespDto> getByJobSeekerEmail(

            Principal principal,

            @RequestParam String jobSeekerEmail,

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "5") int size

    ){
        boolean isAdmin = isAdmin(principal);
        return applicationService.getByJobSeekerEmail(principal.getName(), isAdmin, jobSeekerEmail, page, size);
    }

    @GetMapping("/by-jobseeker-id/{jobSeekerId}")
    public List<ApplicationRespDto> getByJobSeekerId(

            Principal principal,

            @PathVariable long jobSeekerId,

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "5") int size

    ){
        boolean isAdmin = isAdmin(principal);
        return applicationService.getByJobSeekerId(principal.getName(), isAdmin, jobSeekerId, page, size);
    }

    @PutMapping("/update-status/{id}")
    public void updateStatus(

            @PathVariable long id,

            @RequestParam String status

    ){
        applicationService.updateStatus(id, status);
    }

    @GetMapping("/by-joblisting/{jobListingId}")
    public List<ApplicationRespDto> getByJobListingId(

            Principal principal,

            @PathVariable long jobListingId,

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "5") int size

    ) {
        boolean isAdmin = isAdmin(principal);
        return applicationService.getByJobListingId(principal.getName(), isAdmin, jobListingId, page, size);
    }

    @GetMapping("/get-one/{id}")
    public ApplicationRespDto getById(@PathVariable long id) {
        return applicationService.getById(id);
    }

    @DeleteMapping("/withdraw/{id}")
    public void withdraw(Principal principal, @PathVariable long id) {
        boolean isAdmin = isAdmin(principal);
        applicationService.withdraw(principal.getName(), isAdmin, id);
    }

    private boolean isAdmin(Principal principal) {

        return ((UsernamePasswordAuthenticationToken) principal)
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    @GetMapping("/count/{jobListingId}")
    public long countByJobListingId(@PathVariable long jobListingId) {
        return applicationService.countByJobListingId(jobListingId);
    }
    @GetMapping("/my-applications")
    public List<ApplicationRespDto> getMyApplications(
            Principal principal,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return applicationService.getMyApplications(principal.getName(), page, size);
    }

}