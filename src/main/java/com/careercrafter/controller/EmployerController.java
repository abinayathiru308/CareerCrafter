package com.careercrafter.controller;

import com.careercrafter.dto.request.EmployerReqDto;
import com.careercrafter.service.EmployerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/add")
    public void insert(

            @Valid
            @RequestBody
            EmployerReqDto dto

    ){
        employerService.insert(dto);
    }

    @DeleteMapping("/de-activate")
    public void deactivateEmployer(

            @RequestParam String employerUsername

    ){
        employerService.deactivateEmployer(employerUsername);
    }

}