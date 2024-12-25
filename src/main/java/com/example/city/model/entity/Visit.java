package com.example.city.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor // Tạo constructor chứa tất cả các field
@NoArgsConstructor  // Tạo constructor không tham số
@Table(name = "visits", schema = "smart_city")
public class Visit {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "visit_date", nullable = false)
    private Instant visitDate;

    @PrePersist
    protected void onCreate() {
        if (this.visitDate == null) {
            this.visitDate = Instant.now();
        }
    }
}