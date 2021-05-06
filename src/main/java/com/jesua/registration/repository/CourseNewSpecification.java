package com.jesua.registration.repository;

import com.jesua.registration.entity.Course;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;

public class CourseNewSpecification {

    private static Predicate hasStatus(boolean status, Root<Course> root, CriteriaBuilder cb) {
        return cb.equal(root.get("open"), status);
    }

    private static Predicate hasGreaterDate(Instant startDate, Root<Course> root, CriteriaBuilder cb) {
        return cb.greaterThan(root.get("startDate"), startDate);
    }

    public static Specification<Course> courseHasStatusAndIsAfter(boolean status, Instant startDate) {
        return (root, query, cb) -> cb.and(hasStatus(status, root, cb), hasGreaterDate(startDate, root, cb));
    }
}
