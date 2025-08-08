package com.docmate.prescription.repository;

import com.docmate.common.entity.PrescriptionMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionMedicineRepository extends JpaRepository<PrescriptionMedicine, UUID> {

    List<PrescriptionMedicine> findByPrescriptionId(UUID prescriptionId);

    void deleteByPrescriptionId(UUID prescriptionId);
}
