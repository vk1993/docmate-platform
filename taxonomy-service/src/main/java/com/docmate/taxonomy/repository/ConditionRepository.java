package com.docmate.taxonomy.repository;

import com.docmate.common.entity.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, UUID> {
    
    List<Condition> findAllByIsActiveTrue();
    
    List<Condition> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
