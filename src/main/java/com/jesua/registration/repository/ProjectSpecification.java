package com.jesua.registration.repository;

import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.filter.ProjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RequiredArgsConstructor
public class ProjectSpecification implements Specification<Project>{

    private final ProjectFilter filter;

    @Override
    public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (filter.getName() != null) {
            predicate.getExpressions().add(cb.equal(root.get("shortName"), filter.getName()));
        }

        return predicate;
    }
}
