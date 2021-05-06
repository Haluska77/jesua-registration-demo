package com.jesua.registration.repository;

import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.filter.CourseFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RequiredArgsConstructor
public class CourseSpecification implements Specification<Course>{

    private final @NonNull CourseFilter filter;

    @Override
    public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (filter.getOpen() != null) {
            predicate.getExpressions().add(cb.equal(root.get("open"), filter.getOpen()));
        }

        if (filter.getStartDate() != null) {
            predicate.getExpressions().add(cb.greaterThan(root.get("startDate"), filter.getStartDate()));
        }

        if (filter.getUserId() != null) {
            Path<Object> user = root.join("user").get("id");
            predicate.getExpressions().add(cb.equal(user, filter.getUserId()));
        }

        if (!CollectionUtils.isEmpty(filter.getProjectList())) {
            Path<Object> project = root.join("project").get("id");
            predicate.getExpressions().add(project.in(filter.getProjectList()));
        }

        return predicate;
    }
}
