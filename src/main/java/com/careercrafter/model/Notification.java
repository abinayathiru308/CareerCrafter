package com.careercrafter.model;

import jakarta.persistence.*;
        import lombok.*;
        import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    private boolean isRead = false;

    @CreationTimestamp
    private Instant createdOn;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}