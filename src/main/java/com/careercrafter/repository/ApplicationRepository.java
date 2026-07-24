package com.careercrafter.repository;

import com.careercrafter.dto.response.ApplicationRespDto;
import com.careercrafter.model.Application;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("""
            select a.id as applicationId, a.status as status,
                   jl.title as jobTitle, e.companyName as employerName,
                   js.name as jobSeekerName
            from Application a
            join a.jobListing jl
            join jl.employer e
            join a.jobSeeker js
            where js.email = ?1
            """)
    List<ApplicationRespDto> getByJobSeekerEmail(String jobSeekerEmail, Pageable pageable);

}