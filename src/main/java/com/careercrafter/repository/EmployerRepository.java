package com.careercrafter.repository;

import com.careercrafter.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {

    Optional<Employer> findByUser_Username(String username);

}