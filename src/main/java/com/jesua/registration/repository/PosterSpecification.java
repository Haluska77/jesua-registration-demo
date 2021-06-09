package com.jesua.registration.repository;

import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.filter.PosterFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RequiredArgsConstructor
public class PosterSpecification implements Specification<Poster>{

    private final PosterFilter filter;

    @Override
    public Predicate toPredicate(Root<Poster> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (filter.getProjectId() != null) {
            Path<Object> project = root.join("project").get("id");
            predicate.getExpressions().add(cb.equal(project, filter.getProjectId()));
        }

        return predicate;
    }
}
