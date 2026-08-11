package com.careercrafter.repository;

import com.careercrafter.dto.response.EmployerListingCountDto;
import com.careercrafter.dto.response.JobListingRespDto;
import com.careercrafter.model.JobListing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobListingRepository
        extends JpaRepository<JobListing, Long> {

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where c.id = ?1
            and jl.isActive = true
            """)
    List<JobListingRespDto> getByCategoryId(
            long categoryId,
            Pageable pageable
    );

    @Query("""
            select e.companyName as companyName,
                   count(jl.id) as listingCount
            from JobListing jl
            join jl.employer e
            group by e.companyName
            """)
    List<EmployerListingCountDto> getListingCountPerEmployer();

    List<JobListing> findByEmployer_Id(long employerId);

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where jl.id = ?1
            """)
    Optional<JobListingRespDto> getByIdWithNames(long id);

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where jl.isActive = true
            """)
    List<JobListingRespDto> getAllActive(Pageable pageable);

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            """)
    List<JobListingRespDto> getAllForAdmin(Pageable pageable);

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where lower(jl.title) like
                  lower(concat('%', :keyword, '%'))
            """)
    List<JobListingRespDto> searchForAdmin(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where lower(jl.title) like
                  lower(concat('%', :keyword, '%'))
               or lower(e.companyName) like
                  lower(concat('%', :keyword, '%'))
               or lower(jl.location) like
                  lower(concat('%', :keyword, '%'))
               or lower(c.name) like
                  lower(concat('%', :keyword, '%'))
            """)
    List<JobListingRespDto> searchAdmin(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where c.id = ?1
            """)
    List<JobListingRespDto> getAdminJobsByCategory(
            long categoryId,
            Pageable pageable
    );

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where jl.isActive = true
            and (:keyword is null or
                 lower(jl.title) like
                 lower(concat('%', :keyword, '%'))
                 or lower(jl.description) like
                 lower(concat('%', :keyword, '%')))
            and (:location is null or
                 lower(jl.location) like
                 lower(concat('%', :location, '%')))
            and (:minSalary is null or jl.salary >= :minSalary)
            and (:maxSalary is null or jl.salary <= :maxSalary)
            """)
    List<JobListingRespDto> search(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            Pageable pageable
    );

    @Query("""
            select jl.id as id, jl.title as title,
                   jl.salary as salary, jl.location as location,
                   c.name as categoryName,
                   e.companyName as employerName
            from JobListing jl
            join jl.category c
            join jl.employer e
            where e.user.username = ?1
            and jl.isActive = true
            """)
    List<JobListingRespDto> getByEmployerUsername(
            String employerUsername,
            Pageable pageable
    );
    @Query("""
        select count(jl.id)
        from JobListing jl
        where jl.isActive = true
        """)
    long countAllActive();

    @Query("""
        select count(jl.id)
        from JobListing jl
        where jl.isActive = true
        and jl.category.id = ?1
        """)
    long countByCategoryId(long categoryId);
}