package com.docmate.taxonomy.controller;

import com.docmate.common.dto.response.ApiResponse;
import com.docmate.taxonomy.dto.SpecializationDto;
import com.docmate.taxonomy.dto.ConditionDto;
import com.docmate.taxonomy.service.TaxonomyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/taxonomy")
@RequiredArgsConstructor
@Tag(name = "Medical Taxonomy", description = "Medical specializations and conditions APIs")
public class TaxonomyController {
    
    private final TaxonomyService taxonomyService;
    
    @GetMapping("/specializations")
    @Operation(summary = "Get all specializations", description = "Get all active medical specializations")
    public ResponseEntity<List<SpecializationDto>> getSpecializations() {
        List<SpecializationDto> specializations = taxonomyService.getAllSpecializations();
        return ResponseEntity.ok(specializations);
    }
    
    @GetMapping("/specializations/{id}")
    @Operation(summary = "Get specialization by ID", description = "Get medical specialization by ID")
    public ResponseEntity<SpecializationDto> getSpecializationById(@PathVariable UUID id) {
        SpecializationDto specialization = taxonomyService.getSpecializationById(id);
        return ResponseEntity.ok(specialization);
    }
    
    @GetMapping("/conditions")
    @Operation(summary = "Get all conditions", description = "Get all active medical conditions")
    public ResponseEntity<List<ConditionDto>> getConditions() {
        List<ConditionDto> conditions = taxonomyService.getAllConditions();
        return ResponseEntity.ok(conditions);
    }
    
    @GetMapping("/conditions/{id}")
    @Operation(summary = "Get condition by ID", description = "Get medical condition by ID")
    public ResponseEntity<ConditionDto> getConditionById(@PathVariable UUID id) {
        ConditionDto condition = taxonomyService.getConditionById(id);
        return ResponseEntity.ok(condition);
    }
}
