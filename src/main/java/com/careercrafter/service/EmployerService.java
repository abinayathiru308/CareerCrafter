package com.careercrafter.service;

import com.careercrafter.dto.request.EmployerReqDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.mapper.EmployerMapper;
import com.careercrafter.mapper.UserMapper;
import com.careercrafter.model.Employer;
import com.careercrafter.model.JobListing;
import com.careercrafter.model.User;
import com.careercrafter.repository.EmployerRepository;
import com.careercrafter.repository.JobListingRepository;
import com.careercrafter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final JobListingRepository jobListingRepository;
    private final PasswordEncoder passwordEncoder;

    public void insert(EmployerReqDto employerReqDto) {

        User user = UserMapper.convertDtoToEntity(employerReqDto.username(),
                employerReqDto.password(),
                Role.EMPLOYER);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);

        Employer employer = EmployerMapper.convertDtoToEntity(employerReqDto);
        employer.setUser(user);

        employerRepository.save(employer);
    }

    @Transactional
    public void deactivateEmployer(String employerUsername) {

        Employer employer = employerRepository.findByUser_Username(employerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Employer username invalid"));

        employer.getUser().setActivated(false);
        employerRepository.save(employer);

        List<JobListing> list = jobListingRepository.findByEmployer_Id(employer.getId());

        list.forEach(jl -> jl.setActive(false));

        jobListingRepository.saveAll(list);
    }

}