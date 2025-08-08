package com.docmate.prescription.service;

import com.docmate.common.entity.Prescription;
import com.docmate.common.entity.PrescriptionMedicine;
import com.docmate.common.exception.BusinessException;
import com.docmate.prescription.dto.CreatePrescriptionRequest;
import com.docmate.prescription.dto.PrescriptionDto;
import com.docmate.prescription.mapper.PrescriptionMapper;
import com.docmate.prescription.repository.PrescriptionRepository;
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
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionDto createPrescription(CreatePrescriptionRequest request, UUID doctorId) {
        log.info("Creating prescription for appointment: {} by doctor: {}", request.getAppointmentId(), doctorId);

        // Create prescription
        Prescription prescription = Prescription.builder()
                .diagnosis(request.getDiagnosis())
                .symptoms(request.getSymptoms())
                .advice(request.getAdvice())
                .build();

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Create medicines if provided
        if (request.getMedicines() != null && !request.getMedicines().isEmpty()) {
            List<PrescriptionMedicine> medicines = request.getMedicines().stream()
                    .map(medicineRequest -> PrescriptionMedicine.builder()
                            .prescription(savedPrescription)
                            .name(medicineRequest.getName())
                            .dosage(medicineRequest.getDosage())
                            .frequency(medicineRequest.getFrequency())
                            .duration(medicineRequest.getDuration())
                            .instructions(medicineRequest.getInstructions())
                            .build())
                    .toList();

            // In a real implementation, you would save medicines to repository
        }

        log.info("Prescription created successfully with ID: {}", savedPrescription.getId());
        return prescriptionMapper.toDto(savedPrescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionDto> getPatientPrescriptions(UUID patientId) {
        log.info("Fetching prescriptions for patient: {}", patientId);

        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        return prescriptions.stream()
                .map(prescriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrescriptionDto getPrescription(UUID prescriptionId) {
        log.info("Fetching prescription with ID: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new BusinessException("PRESCRIPTION_NOT_FOUND", "Prescription not found", 404));

        return prescriptionMapper.toDto(prescription);
    }
}
