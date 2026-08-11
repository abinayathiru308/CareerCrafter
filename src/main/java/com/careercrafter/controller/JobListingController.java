package com.careercrafter.controller;

import com.careercrafter.dto.request.JobListingReqDto;
import com.careercrafter.dto.response.EmployerListingCountDto;
import com.careercrafter.dto.response.JobListingRespDto;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.service.JobListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/joblisting")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class JobListingController {

    private final JobListingService jobListingService;

    @PostMapping("/add")
    public void insert(

            Principal principal,

            @Valid
            @RequestBody
            JobListingReqDto dto

    ){
        String employerUsername = principal.getName();
        jobListingService.insert(employerUsername, dto);
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

    @GetMapping("/get-one/{id}")
    public JobListingRespDto getById(@PathVariable long id) {
        return jobListingService.getById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(Principal principal, @PathVariable long id) {

        boolean isAdmin = isAdmin(principal);

        jobListingService.delete(principal.getName(), isAdmin, id);
    }

    @PutMapping("/update/{id}")
    public void update(

            Principal principal,

            @PathVariable long id,

            @Valid
            @RequestBody
            JobListingReqDto dto

    ){
        boolean isAdmin = isAdmin(principal);

        jobListingService.update(principal.getName(), isAdmin, id, dto);
    }

    @GetMapping("/my-listings")
    public List<JobListingRespDto> getMyListings(

            Principal principal,

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "10") int size

    ){
        return jobListingService.getByEmployerUsername(principal.getName(), page, size);
    }

    @GetMapping("/search")
    public List<JobListingRespDto> search(

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) String location,

            @RequestParam(required = false) Double minSalary,

            @RequestParam(required = false) Double maxSalary,

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "10") int size

    ){
        return jobListingService.search(keyword, location, minSalary, maxSalary, page, size);
    }

    @GetMapping("/get-all")
    public List<JobListingRespDto> getAll(

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "10") int size

    ){
        return jobListingService.getAllActive(page, size);
    }

    private boolean isAdmin(Principal principal) {

        return ((UsernamePasswordAuthenticationToken) principal)
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    @GetMapping("/admin/all")
    public List<JobListingRespDto> getAllForAdmin(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        if (!isAdmin(principal)) {
            throw new InvalidCredentialsException(
                    "Admin access required"
            );
        }

        return jobListingService.getAllForAdmin(page, size);
    }

    @GetMapping("/admin/search")
    public List<JobListingRespDto> searchAdmin(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        if (!isAdmin(principal)) {
            throw new InvalidCredentialsException(
                    "Admin access required"
            );
        }

        return jobListingService.searchAdmin(
                keyword,
                page,
                size
        );
    }

    @GetMapping("/admin/category/{categoryId}")
    public List<JobListingRespDto> getAdminJobsByCategory(
            Principal principal,
            @PathVariable long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        if (!isAdmin(principal)) {
            throw new InvalidCredentialsException(
                    "Admin access required"
            );
        }

        return jobListingService.getAdminJobsByCategory(
                categoryId,
                page,
                size
        );
    }
    @GetMapping("/count/all")
    public long getTotalActiveJobs() {
        return jobListingService.getTotalActiveJobs();
    }
    @GetMapping("/count/category/{categoryId}")
    public long getTotalJobsByCategory(
            @PathVariable long categoryId
    ) {
        return jobListingService.getTotalJobsByCategory(categoryId);
    }

}