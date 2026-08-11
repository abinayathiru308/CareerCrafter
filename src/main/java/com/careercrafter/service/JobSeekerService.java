package com.careercrafter.service;

import com.careercrafter.dto.request.JobSeekerDto;
import com.careercrafter.dto.response.JobSeekerProfileRespDto;
import com.careercrafter.dto.response.JobSeekerRespDto;
import com.careercrafter.dto.response.UploadDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.mapper.JobSeekerMapper;
import com.careercrafter.mapper.UserMapper;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.model.User;
import com.careercrafter.repository.JobSeekerRepository;
import com.careercrafter.repository.UserRepository;
import com.careercrafter.utility.UploadUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadUtility uploadUtility;
    private static final String uploadPath =
            "C:\\Users\\infinix\\careercrafter-frontend\\public\\resumes";


    public JobSeeker add(JobSeekerDto dto) {

        User user = UserMapper.convertDtoToEntity(
                dto.username(),
                dto.password(),
                Role.JOBSEEKER
        );

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        user = userRepository.save(user);

        JobSeeker jobSeeker =
                JobSeekerMapper.mapDtoToEntity(dto);

        jobSeeker.setUser(user);

        return jobSeekerRepository.save(jobSeeker);
    }


    public List<JobSeekerRespDto> getAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<JobSeeker> list =
                jobSeekerRepository.fetchAll(pageable).getContent();

        return list
                .stream()
                .map(JobSeekerMapper::mapEntityToDto)
                .toList();
    }


    public JobSeekerRespDto getById(long id) {

        JobSeeker jobSeeker =
                jobSeekerRepository.fetchById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker id Invalid"
                                )
                        );

        return JobSeekerMapper.mapEntityToDto(jobSeeker);
    }


    public void delete(String username, boolean isAdmin, long id) {

        JobSeeker jobSeeker =
                jobSeekerRepository.fetchById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker id Invalid"
                                )
                        );

        boolean isOwner =
                jobSeeker.getUser()
                        .getUsername()
                        .equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException(
                    "Not authorized to delete this profile"
            );
        }

        jobSeeker.setActive(false);

        jobSeekerRepository.save(jobSeeker);
    }


    public void update(
            String username,
            boolean isAdmin,
            long id,
            @Valid JobSeekerDto dto
    ) {

        JobSeeker jobSeekerDB =
                jobSeekerRepository.fetchById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker id Invalid"
                                )
                        );

        boolean isOwner =
                jobSeekerDB.getUser()
                        .getUsername()
                        .equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException(
                    "Not authorized to update this profile"
            );
        }

        jobSeekerDB.setName(dto.name());
        jobSeekerDB.setEmail(dto.email());
        jobSeekerDB.setPhone(dto.phone());
        jobSeekerDB.setSkills(dto.skills());

        jobSeekerRepository.save(jobSeekerDB);
    }


    public void updateResume(
            String username,
            boolean isAdmin,
            long id,
            String resumeUrl
    ) {

        JobSeeker jobSeeker =
                jobSeekerRepository.fetchById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker id Invalid"
                                )
                        );

        boolean isOwner =
                jobSeeker.getUser()
                        .getUsername()
                        .equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException(
                    "Not authorized to update this resume"
            );
        }

        jobSeeker.setResumeUrl(resumeUrl);

        jobSeekerRepository.save(jobSeeker);
    }


    public String getResume(long id) {

        JobSeeker jobSeeker =
                jobSeekerRepository.fetchById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker id Invalid"
                                )
                        );

        return jobSeeker.getResumeUrl();
    }


    public void deleteResume(
            String username,
            boolean isAdmin,
            long id
    ) {

        JobSeeker jobSeeker =
                jobSeekerRepository.fetchById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker id Invalid"
                                )
                        );

        boolean isOwner =
                jobSeeker.getUser()
                        .getUsername()
                        .equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException(
                    "Not authorized to delete this resume"
            );
        }

        jobSeeker.setResumeUrl(null);

        jobSeekerRepository.save(jobSeeker);
    }


    public JobSeekerProfileRespDto getMyProfile(String username) {

        JobSeeker jobSeeker =
                jobSeekerRepository.fetchByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker profile not found"
                                )
                        );

        return new JobSeekerProfileRespDto(
                jobSeeker.getId(),
                jobSeeker.getName(),
                jobSeeker.getEmail(),
                jobSeeker.getPhone(),
                jobSeeker.getSkills()
        );
    }


    public UploadDto uploadResume(
            String username,
            boolean isAdmin,
            long id,
            MultipartFile resumeFile
    ) throws IOException {

        JobSeeker jobSeeker =
                jobSeekerRepository.fetchById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "JobSeeker id Invalid"
                                )
                        );

        boolean isOwner =
                jobSeeker.getUser()
                        .getUsername()
                        .equals(username);

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException(
                    "Not authorized to upload resume for this profile"
            );
        }

        uploadUtility.validateResume(resumeFile);

        Path uPathDir = Paths.get(uploadPath);

        if (!Files.exists(uPathDir)) {
            Files.createDirectories(uPathDir);
        }


        String fileName =
                Objects.requireNonNull(
                        resumeFile.getOriginalFilename()
                );


        Path filePath =
                uPathDir.resolve(fileName);

        Files.copy(
                resumeFile.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        String resumeUrl = "/resumes/" + fileName;

        jobSeeker.setResumeUrl(resumeUrl);

        jobSeeker =
                jobSeekerRepository.save(jobSeeker);

        return new UploadDto(
                jobSeeker.getId(),
                jobSeeker.getResumeUrl(),
                fileName,
                "File upload success"
        );
    }
}