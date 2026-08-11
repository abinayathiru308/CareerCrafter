package com.careercrafter.model;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String status;
    private String batch;
    private String course;
    private String certifications;
    private String college;
    private int yearPassedOut;
    private String skills;
    @ManyToOne
    @JoinColumn(name = "joblisting_id", nullable = false)
    private JobListing jobListing;
    @ManyToOne
    @JoinColumn(name = "jobseeker_id", nullable = false)
    private JobSeeker jobSeeker;
}