package com.jesua.registration.repository;

import com.jesua.registration.entity.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PosterRepository extends JpaRepository<Poster, Long>, JpaSpecificationExecutor<Poster> {

}
