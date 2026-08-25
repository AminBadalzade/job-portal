package com.amin.jobportal.specification;

import com.amin.jobportal.entity.Job;
import com.amin.jobportal.enums.EmploymentType;
import com.amin.jobportal.enums.ExperienceLevel;
import com.amin.jobportal.enums.WorkType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class JobSpecs {
    public static Specification<Job> containsTitle(String title){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Job> hasCity(String city){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("city"), city);
    }

    public static Specification<Job> hasCountry(String country){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("country"), country);
    }

    public static Specification<Job> hasExperienceLevel(ExperienceLevel experienceLevel){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("experienceLevel"), experienceLevel);
    }

    public static Specification<Job> hasWorkType(WorkType workType){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("workType"), workType);
    }

    public static Specification<Job> hasEmploymentType(EmploymentType employmentType){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("employmentType"), employmentType);
    }

    public static Specification<Job> hasSalaryMin(BigDecimal salaryMin) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("salaryMin"), salaryMin);
    }

    public static Specification<Job> hasSalaryMax(BigDecimal salaryMax) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("salaryMax"), salaryMax);
    }
}
