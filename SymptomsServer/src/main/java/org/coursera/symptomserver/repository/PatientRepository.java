package org.coursera.symptomserver.repository;

import java.util.List;
import org.coursera.symptomserver.beans.jpa.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository class to access database Patient Table
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{

	/**
	 * Returns a Patient with login that corresponds to login parameter
	 * @param login a String with login data
	 * @return a Patient object
	 * @see org.coursera.symptomserver.beans.Patient 
	 */
    public Patient findByLogin(String login);

    /**
     * Returns all patients that are associated with doctorUserName
     *    
     * @param doctorUserName a String with doctor's userName
     * @return a List<Patient>
     * @see org.coursera.symptomserver.beans.Patient  
     */
    @Query("select p from Patient p where p.doctor.login = :doctorUserName order by p.name, p.lastname")
    public List<Patient> findAllPatientsByDoctor(@Param("doctorUserName") String doctorUserName);
    
    /**
     * Returns a patient with name field that corresponds to patientName and associated to doctor's Id
     * 
     * @param doctorId a Long with doctor Id in the databse
     * @param patientName a String with patient's name
     * @return a Patient object
     * @see org.coursera.symptomserver.beans.Patient  
     */
    @Query("select p from Patient p where p.doctor.id = :doctorId and p.name = :patientName")
    public Patient findByName(@Param("doctorId") long doctorId, @Param("patientName") String patientName);
}
