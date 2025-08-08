package com.docmate.taxonomy.repository;

import com.docmate.common.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
    
    List<Specialization> findAllByIsActiveTrue();
    
    List<Specialization> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
