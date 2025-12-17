package com.skillconnect.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "services")
@Data
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)  // Adjust length as needed
    private String description;

    private String category;

    @ElementCollection
    private List<String> imageUrls;

    @CreationTimestamp
    private LocalDateTime createdAt; // Automatically set to the current date and time when the entity is created

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // Foreign key column
    private User postedBy;

    @Column(nullable = false)
    private boolean isActive = true;

}
