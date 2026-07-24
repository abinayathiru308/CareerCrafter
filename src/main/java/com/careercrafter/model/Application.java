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
    @ManyToOne
    @JoinColumn(name = "joblisting_id", nullable = false)
    private JobListing jobListing;
    @ManyToOne
    @JoinColumn(name = "jobseeker_id", nullable = false)
    private JobSeeker jobSeeker;
}