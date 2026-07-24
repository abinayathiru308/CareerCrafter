package com.careercrafter.model;
import jakarta.persistence.*;
        import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JobSeeker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String email;
    private String skills;
    private boolean isActive = true;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}