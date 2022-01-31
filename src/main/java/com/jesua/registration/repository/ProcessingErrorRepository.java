package com.jesua.registration.repository;

import com.jesua.registration.entity.ProcessingError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingErrorRepository extends JpaRepository<ProcessingError, Long> {

}
