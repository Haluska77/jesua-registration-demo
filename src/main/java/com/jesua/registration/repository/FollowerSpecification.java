package com.jesua.registration.repository;

import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.filter.FollowerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RequiredArgsConstructor
public class FollowerSpecification implements Specification<Follower>{

    private final FollowerFilter filter;

    @Override
    public Predicate toPredicate(Root<Follower> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (filter.getToken() != null) {
            predicate.getExpressions().add(cb.equal(root.get("token"), filter.getToken()));
        }

        if (!CollectionUtils.isEmpty(filter.getProjects())) {
            Path<Object> project = root.join("course").get("project").get("id");
            predicate.getExpressions().add(project.in(filter.getProjects()));
        }

        return predicate;
    }
}
