package com.careercrafter.model;

import jakarta.persistence.*;
        import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String companyName;
    private String city;

    private boolean isActive = true;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}