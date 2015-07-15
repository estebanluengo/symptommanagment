package org.coursera.symptomserver.repository;

import org.coursera.symptomserver.beans.jpa.PatientMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class to access database PatientMedication Table
 */
@Repository
public interface PatientMedicationRepository extends JpaRepository<PatientMedication, Long>{
    
}
