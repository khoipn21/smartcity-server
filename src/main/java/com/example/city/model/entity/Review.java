package com.example.city.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "reviews", schema = "smart_city")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @Column(name = "rating")
    private Byte rating;

    @Lob
    @Column(name = "comment")
    private String comment;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        // updatedAt = now;
    }

    // @PreUpdate
    // protected void onUpdate() {
    //     updatedAt = Instant.now();
    // }
}