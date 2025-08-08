package com.docmate.taxonomy.service;

import com.docmate.common.entity.Specialization;
import com.docmate.common.entity.Condition;
import com.docmate.common.exception.BusinessException;
import com.docmate.taxonomy.dto.SpecializationDto;
import com.docmate.taxonomy.dto.ConditionDto;
import com.docmate.taxonomy.repository.SpecializationRepository;
import com.docmate.taxonomy.repository.ConditionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxonomyService {
    
    private final SpecializationRepository specializationRepository;
    private final ConditionRepository conditionRepository;
    
    public List<SpecializationDto> getAllSpecializations() {
        log.info("Fetching all active specializations");
        return specializationRepository.findAllByIsActiveTrue().stream()
                .map(this::mapToSpecializationDto)
                .collect(Collectors.toList());
    }
    
    public SpecializationDto getSpecializationById(UUID id) {
        log.info("Fetching specialization by ID: {}", id);
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("SPECIALIZATION_NOT_FOUND", "Specialization not found", 404));
        return mapToSpecializationDto(specialization);
    }
    
    public List<ConditionDto> getAllConditions() {
        log.info("Fetching all active conditions");
        return conditionRepository.findAllByIsActiveTrue().stream()
                .map(this::mapToConditionDto)
                .collect(Collectors.toList());
    }
    
    public ConditionDto getConditionById(UUID id) {
        log.info("Fetching condition by ID: {}", id);
        Condition condition = conditionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CONDITION_NOT_FOUND", "Condition not found", 404));
        return mapToConditionDto(condition);
    }
    
    public List<SpecializationDto> searchSpecializations(String name) {
        log.info("Searching specializations by name: {}", name);
        return specializationRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name).stream()
                .map(this::mapToSpecializationDto)
                .collect(Collectors.toList());
    }
    
    public List<ConditionDto> searchConditions(String name) {
        log.info("Searching conditions by name: {}", name);
        return conditionRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name).stream()
                .map(this::mapToConditionDto)
                .collect(Collectors.toList());
    }
    
    private SpecializationDto mapToSpecializationDto(Specialization specialization) {
        return SpecializationDto.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .description(specialization.getDescription())
                .isActive(specialization.getIsActive())
                .createdDate(specialization.getCreatedDate())
                .build();
    }
    
    private ConditionDto mapToConditionDto(Condition condition) {
        return ConditionDto.builder()
                .id(condition.getId())
                .name(condition.getName())
                .description(condition.getDescription())
                .isActive(condition.getIsActive())
                .createdDate(condition.getCreatedDate())
                .build();
    }
}
