package com.careercrafter.repository;

import com.careercrafter.dto.response.SavedJobRespDto;
import com.careercrafter.model.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    @Query("""
            select sj.id as id, jl.title as jobTitle,
                   e.companyName as companyName, sj.savedOn as savedOn
            from SavedJob sj
            join sj.jobListing jl
            join jl.employer e
            where sj.jobSeeker.id = ?1
            """)
    List<SavedJobRespDto> getByJobSeekerId(long jobSeekerId);

}