package com.careercrafter.controller;

import com.careercrafter.dto.request.JobSeekerDto;
import com.careercrafter.dto.response.JobSeekerProfileRespDto;
import com.careercrafter.dto.response.JobSeekerRespDto;
import com.careercrafter.dto.response.UploadDto;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.service.JobSeekerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/jobseeker/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;

    @PostMapping("/add")
    public JobSeeker add(@Valid @RequestBody JobSeekerDto jobSeekerDto){
        return jobSeekerService.add(jobSeekerDto);
    }

    @GetMapping("/get-all")
    public List<JobSeekerRespDto> getAll(@RequestParam Integer page,
                                         @RequestParam Integer size){
        return jobSeekerService.getAll(page,size);
    }

    @GetMapping("/get-one/{id}")
    public JobSeekerRespDto getById(@PathVariable long id){
        return jobSeekerService.getById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(Principal principal, @PathVariable long id){
        boolean isAdmin = isAdmin(principal);
        jobSeekerService.delete(principal.getName(), isAdmin, id);
    }

    @PutMapping("/update/{id}")
    public void update(Principal principal,
                       @PathVariable long id,
                       @Valid @RequestBody JobSeekerDto jobSeekerDto){
        boolean isAdmin = isAdmin(principal);
        jobSeekerService.update(principal.getName(), isAdmin, id, jobSeekerDto);
    }

    @PutMapping("/update-resume/{id}")
    public void updateResume(Principal principal,
                             @PathVariable long id,
                             @RequestParam String resumeUrl) {
        boolean isAdmin = isAdmin(principal);
        jobSeekerService.updateResume(principal.getName(), isAdmin, id, resumeUrl);
    }

    @GetMapping("/get-resume/{id}")
    public String getResume(@PathVariable long id) {
        return jobSeekerService.getResume(id);
    }

    @DeleteMapping("/delete-resume/{id}")
    public void deleteResume(Principal principal, @PathVariable long id) {
        boolean isAdmin = isAdmin(principal);
        jobSeekerService.deleteResume(principal.getName(), isAdmin, id);
    }

    private boolean isAdmin(Principal principal) {

        return ((UsernamePasswordAuthenticationToken) principal)
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }
    @GetMapping("/my-profile")
    public JobSeekerProfileRespDto getMyProfile(Principal principal) {
        return jobSeekerService.getMyProfile(principal.getName());
    }

    @PostMapping("/upload-resume/{id}")
    public UploadDto uploadResume(Principal principal,
                                  @PathVariable long id,
                                  @RequestParam("resumeFile") MultipartFile resumeFile) throws IOException, InterruptedException {
        boolean isAdmin = isAdmin(principal);
        return jobSeekerService.uploadResume(principal.getName(), isAdmin, id, resumeFile);
    }

}