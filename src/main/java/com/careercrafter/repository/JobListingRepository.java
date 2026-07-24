package com.careercrafter.repository;

import com.careercrafter.dto.response.EmployerListingCountDto;
import com.careercrafter.dto.response.JobListingRespDto;
import com.careercrafter.model.JobListing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    @Query("""
            select jl.id as id, jl.title as title, jl.salary as salary,
                   c.name as categoryName, e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where c.id = ?1
            """)
    List<JobListingRespDto> getByCategoryId(long categoryId, Pageable pageable);

    @Query("""
            select e.companyName as companyName, count(jl.id) as listingCount
            from JobListing jl
            join jl.employer e
            group by e.companyName
            """)
    List<EmployerListingCountDto> getListingCountPerEmployer();

    List<JobListing> findByEmployer_Id(long employerId);

}