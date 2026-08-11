package com.careercrafter.service;

import com.careercrafter.dto.request.EmployerReqDto;
import com.careercrafter.dto.response.EmployerRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final JobListingRepository jobListingRepository;
    private final PasswordEncoder passwordEncoder;


    public EmployerRespDto getById(long id) {

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer id invalid"));

        return EmployerMapper.convertEntityToDto(employer);
    }

    public List<EmployerRespDto> getAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<Employer> list = employerRepository.findAll(pageable).getContent();

        return list
                .stream()
                .map(EmployerMapper::convertEntityToDto)
                .toList();
    }

    @Transactional
    public void deactivateEmployer(String requesterUsername, boolean isAdmin, String employerUsername) {

        boolean isSelf = requesterUsername.equals(employerUsername);

        if (!isSelf && !isAdmin) {
            throw new InvalidCredentialsException("Not authorized to deactivate this employer");
        }

        Employer employer = employerRepository.findByUser_Username(employerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Employer username invalid"));

        employer.getUser().setActivated(false);
        employerRepository.save(employer);

        List<JobListing> list = jobListingRepository.findByEmployer_Id(employer.getId());

        list.forEach(jl -> jl.setActive(false));

        jobListingRepository.saveAll(list);
    }

    public void update(String username, boolean isAdmin, long id, @Valid EmployerReqDto dto) {

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer id invalid"));

        boolean isOwner = employer.getUser().getUsername().equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException("Not authorized to update this employer");
        }

        employer.setCompanyName(dto.companyName());
        employer.setCity(dto.city());

        employerRepository.save(employer);
    }

    @Transactional
    public void insert(EmployerReqDto dto) {

        boolean usernameExists = userRepository.existsByUsername(dto.username());
        if (usernameExists) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = UserMapper.convertDtoToEntity(dto.username(), dto.password(), Role.EMPLOYER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        Employer employer = EmployerMapper.convertDtoToEntity(dto);
        employer.setUser(user);

        employerRepository.save(employer);
    }

}