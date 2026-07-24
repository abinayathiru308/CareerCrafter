package com.careercrafter.repository;
import com.careercrafter.model.JobSeeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
public interface JobSeekerRepository extends JpaRepository<JobSeeker,Long> {
    @Query("""
            select j
            from JobSeeker j
            where j.id = ?1 AND j.isActive = true
            """)
    Optional<JobSeeker> fetchById(long id);
    @Query("""
            select j
            from JobSeeker j
            where j.isActive = true
            """)
    Page<JobSeeker> fetchAll(Pageable pageable);
}