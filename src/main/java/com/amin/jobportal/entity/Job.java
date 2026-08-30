package com.amin.jobportal.entity;

import com.amin.jobportal.enums.EmploymentType;
import com.amin.jobportal.enums.ExperienceLevel;
import com.amin.jobportal.enums.JobStatus;
import com.amin.jobportal.enums.WorkType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2, name = "salary_min")
    private BigDecimal salaryMin;

    @Column(precision = 10, scale = 2, name = "salary_max")
    private BigDecimal salaryMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "work_type")
    private WorkType workType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "employment_type")
    private EmploymentType employmentType;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false, name = "expires_at")
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


}
