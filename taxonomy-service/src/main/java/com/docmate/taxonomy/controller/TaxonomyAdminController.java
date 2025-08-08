package com.docmate.taxonomy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Administrative endpoints for managing taxonomy data such as specialisations and conditions.
 */
@RestController
@RequestMapping("/api/admin/taxonomy")
public class TaxonomyAdminController {

    @GetMapping("/specialisations")
    public List<String> listSpecialisations() {
        return List.of("Cardiology", "Dermatology", "Neurology");
    }

    @PostMapping("/specialisations")
    public ResponseEntity<Void> createSpecialisation(@RequestBody String name) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/specialisations/{id}")
    public ResponseEntity<Void> updateSpecialisation(@PathVariable Long id, @RequestBody String name) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/specialisations/{id}")
    public ResponseEntity<Void> deleteSpecialisation(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conditions")
    public List<String> listConditions() {
        return List.of("Hypertension", "Diabetes", "Asthma");
    }

    @PostMapping("/conditions")
    public ResponseEntity<Void> createCondition(@RequestBody String name) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/conditions/{id}")
    public ResponseEntity<Void> updateCondition(@PathVariable Long id, @RequestBody String name) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/conditions/{id}")
    public ResponseEntity<Void> deleteCondition(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}