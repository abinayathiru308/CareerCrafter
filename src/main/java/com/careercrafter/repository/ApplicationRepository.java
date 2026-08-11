package com.careercrafter.repository;

import com.careercrafter.dto.response.ApplicationRespDto;
import com.careercrafter.model.Application;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("""
            select a.id as applicationId, a.status as status,
                   jl.title as jobTitle, e.companyName as employerName,
                   js.name as jobSeekerName, js.resumeUrl as resumeUrl,
                   a.batch as batch, a.course as course, a.certifications as certifications,
                   a.college as college, a.yearPassedOut as yearPassedOut, a.skills as skills
            from Application a
            join a.jobListing jl
            join jl.employer e
            join a.jobSeeker js
            where js.email = ?1
            """)
    List<ApplicationRespDto> getByJobSeekerEmail(String jobSeekerEmail, Pageable pageable);

    @Query("""
            select a.id as applicationId, a.status as status,
                   jl.title as jobTitle, e.companyName as employerName,
                   js.name as jobSeekerName, js.resumeUrl as resumeUrl,
                   a.batch as batch, a.course as course, a.certifications as certifications,
                   a.college as college, a.yearPassedOut as yearPassedOut, a.skills as skills
            from Application a
            join a.jobListing jl
            join jl.employer e
            join a.jobSeeker js
            where jl.id = ?1
            """)
    List<ApplicationRespDto> getByJobListingId(long jobListingId, Pageable pageable);

    @Query("""
            select a.id as applicationId, a.status as status,
                   jl.title as jobTitle, e.companyName as employerName,
                   js.name as jobSeekerName, js.resumeUrl as resumeUrl,
                   a.batch as batch, a.course as course, a.certifications as certifications,
                   a.college as college, a.yearPassedOut as yearPassedOut, a.skills as skills
            from Application a
            join a.jobListing jl
            join jl.employer e
            join a.jobSeeker js
            where a.id = ?1
            """)
    Optional<ApplicationRespDto> getOneById(long id);

    @Query("""
            select a.id as applicationId, a.status as status,
                   jl.title as jobTitle, e.companyName as employerName,
                   js.name as jobSeekerName, js.resumeUrl as resumeUrl,
                   a.batch as batch, a.course as course, a.certifications as certifications,
                   a.college as college, a.yearPassedOut as yearPassedOut, a.skills as skills
            from Application a
            join a.jobListing jl
            join jl.employer e
            join a.jobSeeker js
            where js.id = ?1
            """)
    List<ApplicationRespDto> getByJobSeekerId(long jobSeekerId, Pageable pageable);

    @Query("""
        select count(a.id)
        from Application a
        where a.jobListing.id = ?1
        """)
    long countByJobListingId(long jobListingId);

    @Query("""
        select case when count(a.id) > 0 then true else false end
        from Application a
        where a.jobSeeker.id = ?1
        and a.jobListing.id = ?2
        """)
    boolean existsByJobSeekerIdAndJobListingId(long jobSeekerId, long jobListingId);

}