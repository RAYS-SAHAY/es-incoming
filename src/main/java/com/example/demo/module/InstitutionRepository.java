package com.example.demo.module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Integer> {
    List<Institution> findInstitutionsByStatus(Integer status);

    Optional<Institution> findInstitutionByInstId(Integer inst);
}
