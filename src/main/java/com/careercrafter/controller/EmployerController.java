package com.careercrafter.controller;

import com.careercrafter.dto.request.EmployerReqDto;
import com.careercrafter.dto.response.EmployerRespDto;
import com.careercrafter.service.EmployerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployerController {

    private final EmployerService employerService;


    @GetMapping("/get-one/{id}")
    public EmployerRespDto getById(@PathVariable long id) {
        return employerService.getById(id);
    }

    @GetMapping("/get-all")
    public List<EmployerRespDto> getAll(

            @RequestParam(required = false, defaultValue = "0") int page,

            @RequestParam(required = false, defaultValue = "10") int size

    ){
        return employerService.getAll(page, size);
    }

    @DeleteMapping("/de-activate")
    public void deactivateEmployer(

            Principal principal,

            @RequestParam String employerUsername

    ){
        boolean isAdmin = isAdmin(principal);
        employerService.deactivateEmployer(principal.getName(), isAdmin, employerUsername);
    }

    @PutMapping("/update/{id}")
    public void update(Principal principal, @PathVariable long id, @Valid @RequestBody EmployerReqDto dto) {
        boolean isAdmin = isAdmin(principal);
        employerService.update(principal.getName(), isAdmin, id, dto);
    }

    @PostMapping("/add")
    public String insert(@Valid @RequestBody EmployerReqDto dto) {
        employerService.insert(dto);
        return "Employer registration successful!";
    }

    private boolean isAdmin(Principal principal) {

        return ((UsernamePasswordAuthenticationToken) principal)
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

}