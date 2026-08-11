package com.careercrafter.mapper;

import com.careercrafter.dto.request.JobSeekerDto;
import com.careercrafter.dto.response.JobSeekerRespDto;
import com.careercrafter.model.JobSeeker;

public class JobSeekerMapper {

    public static JobSeeker mapDtoToEntity(JobSeekerDto dto) {

        JobSeeker jobSeeker = new JobSeeker();

        jobSeeker.setName(dto.name());
        jobSeeker.setEmail(dto.email());
        jobSeeker.setPhone(dto.phone());
        jobSeeker.setSkills(dto.skills());

        return jobSeeker;
    }

    public static JobSeekerRespDto mapEntityToDto(JobSeeker jobSeeker) {

        return new JobSeekerRespDto(

                jobSeeker.getName(),
                jobSeeker.getEmail(),
                jobSeeker.getPhone(),
                jobSeeker.getSkills()

        );
    }

}