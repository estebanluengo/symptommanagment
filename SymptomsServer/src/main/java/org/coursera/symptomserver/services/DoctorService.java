package org.coursera.symptomserver.services;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.coursera.symptomserver.beans.Status;
import org.coursera.symptomserver.beans.jpa.Checkin;
import org.coursera.symptomserver.beans.jpa.Doctor;
import org.coursera.symptomserver.beans.jpa.Patient;
import org.coursera.symptomserver.beans.jpa.PatientMedication;
import org.coursera.symptomserver.exceptions.NotFoundException;
import org.coursera.symptomserver.handlers.SpringSecurityHandler;
import org.coursera.symptomserver.repository.CheckinRepository;
import org.coursera.symptomserver.repository.DoctorRepository;
import org.coursera.symptomserver.repository.PatientMedicationRepository;
import org.coursera.symptomserver.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Doctors business operations
 */
@Service
@Transactional(rollbackForClassName = "Throwable")
@SuppressWarnings("unused")
public class DoctorService {

    private static final Logger logger = Logger.getLogger(DoctorService.class);
    //Max number of results for checkins patient list.
    public static final int MAX_CHECKIN_RESULTS = 20;  
    
    //Object to access Doctor table
    @Autowired
    private DoctorRepository doctorRepository;

    //Object to access Patient table
    @Autowired
    private PatientRepository patientRepository;

    //Object to access Checkin table 
    @Autowired
    private CheckinRepository checkinRepository;
    
    //Object to access PatientMedication table
    @Autowired
    private PatientMedicationRepository patientMedicationRepository;
    
    /**
     * Returns a object Doctor associated to userName
     * 
     * @param userName a String with userName of logged user
     * @return a Doctor object
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     * @see org.coursera.symptomserver.beans.Doctor
     */
    private Doctor getDoctor(String userName) {
        Doctor doctor = doctorRepository.findByLogin(userName);
        if (doctor == null) {
            logger.debug("Doctor not found for userName:" + userName);
            throw new NotFoundException("DOCTOR_NOT_FOUND");
        } else {
            logger.debug("Doctor found with ID:" + doctor.getId());
            return doctor;
        }
    }

    /**
     * Returns a status information about doctor logged. The method query Doctor
     * table by the userName parameter.
     *
     * @param userName a String that represents userName of logged user
     * @return a Status object with doctor information
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     * @see org.coursera.symptomserver.beans.Status
     */
    public Status getStatus(String userName) {
        logger.info("Calling getStatus for userName:" + userName);
        //load doctor from database for security reasons
        Doctor doctor = getDoctor(userName);
        //build status object to return
        logger.debug("Doctor found with ID:" + doctor.getId());
        Status status = new Status();
        status.setDoctor(doctor);
        status.setRole(SpringSecurityHandler.DOCTOR_ROLE);
        logger.debug("Returning status for userName:" + userName);
        return status;
    }

    /**
     * Returns a Doctor's patients list.
     * 
     * @param userName a String that represents userName of logged user     
     * @return a List<Patient>
     * @see org.coursera.symptomserver.beans.jpa.Patient
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     */
    public List<Patient> getPatients(String userName) {
        logger.info("Calling getPatients for userName:" + userName);
        //load doctor from database for security reasons
        Doctor doctor = getDoctor(userName);
        List<Patient> patientList = patientRepository.findAllPatientsByDoctor(userName);
        logger.debug("Returning patient list for doctor userName:" + userName);
        return patientList;
    }
    
    /**
     * This method returns a Patient information that corresponds with patient name and associaded to
     * doctor userName
     * 
     * @param userName a String that represents userName of logged user 
     * @param patientName a String with patient name
     * 
     * @return a Patient object. Nor Patient's checkin information neither Patient's medication is returned.
     * @see org.coursera.symptomserver.beans.jpa.Patient
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     */
    public Patient getPatient(String userName, String patientName){
    	logger.info("Calling getPatient for userName:" + userName);
        //load doctor from database for security reasons
        Doctor doctor = getDoctor(userName);
        Patient patient = patientRepository.findByName(doctor.getId(), patientName);
        logger.debug("Returning patient for doctor userName:" + userName);
        return patient;
        
    }

    /**
     * This method returns Doctor's patient check-ins with Patient Id patientId
     * The method only returns the last MAX_CHECKIN_RESULTS check-ins
     * 
     * @param userName a String that represents userName of logged user
     * @param patientId a Long with Patiend Id
     * @return an ArrayList<Checkin>. Every Checkin object contains CheckinMedication list
     * @see org.coursera.symptomserver.beans.jpa.Checkin
     * @see org.coursera.symptomserver.beans.jpa.CheckinMedication
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     */
    public List<Checkin> getPatientCheckins(String userName, Long patientId) {
        logger.info("Calling getPatientCheckins for userName:" + userName+ "and patientId:"+patientId);
        //load doctor from database for security reasons
        Doctor doctor = getDoctor(userName);
        Pageable topTen = new PageRequest(0, MAX_CHECKIN_RESULTS);
        List<Checkin> checkinList = checkinRepository.findAllCheckins(userName, patientId, topTen);
        logger.debug("Returning patient checkins list for doctor userName:" + userName);
        return checkinList;
    }
    
    /**
     * This method returns Doctor's patient check-ins with different criteria.
     * The method has four possibilities: 
     * 1) Search Doctor patient's check-ins for a particular Patient with name patientName. 
     * In this case method returns the last MAX_CHECKIN_RESULTS check-ins.
     * 2) Search Doctor patients' check-ins from a specific date: dateFrom. 
     * In this case method returns all doctor's patients check-ins from a date
     * 3) Search Doctor patient's check-ins for a particular Patient with name patientName and 
     * from a specific date: dateFrom.
     * In this case method returns all specific doctor patient's check-ins from a date
     * 4) Search Doctor patients' check-ins not sent yet to doctor mobile app.
     * In this case method returns all doctor's patients check-ins not sent yet.
     * 
     * @param userName a String that represents userName of logged user
     * @param patientName a String with patient's name that we want his/her check-ins. It can be null
     * @param dateFrom a Date object with the date from we want patients check-ins. It can be null
     * 
     * @return an ArrayList<Checkin> objects. Every Checkin object contains CheckinMedication list
     * @see org.coursera.symptomserver.beans.jpa.Checkin
     * @see org.coursera.symptomserver.beans.jpa.CheckinMedication
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     */
    public List<Checkin> getPatientCheckins(String userName, String patientName, Date dateFrom) {
        logger.info("Calling getPatientCheckins for userName:" + userName+ "and patient name:"+patientName+" from:"+dateFrom);
        //load doctor from database for security reasons
        Doctor doctor = getDoctor(userName);
        Pageable top = new PageRequest(0, MAX_CHECKIN_RESULTS);
        List<Checkin> checkinList = null;
        if (patientName != null && dateFrom == null){
            logger.debug("Searching by patient name");
            checkinList = checkinRepository.findAllCheckins(userName, patientName, top);
        }else if (dateFrom != null && patientName == null){
            logger.debug("Searching by date from");
            checkinList = checkinRepository.findAllCheckins(userName, dateFrom);
        }else if (dateFrom != null && patientName != null){
            logger.debug("Searching by patient name and date from");
            checkinList = checkinRepository.findAllCheckins(userName, patientName, dateFrom);
        }else{
        	logger.debug("Searching by not send it yet");
        	checkinList = checkinRepository.findAllCheckinsBySend(userName, false);
        }
        logger.debug("Returning patient checkins list for doctor userName:" + userName);
        return checkinList;    
    }

    /**
     * This method creates new Patient medication information.
     * 
     * @param userName a String that represents userName of logged user
     * @param patientId a Long with database patient Id
     * @param medicationName a String with the medication name to be created for patient.
     * @return the new PatientMedication object created with the database Id.
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     */
    public PatientMedication createPatientMedication(String userName, Long patientId, String medicationName) {
        logger.info("Calling createPatientMedication for userName:" + userName+ "and patientId:"+patientId);
        //load doctor from database for security reasons
        Doctor doctor = getDoctor(userName);
        PatientMedication pm = new PatientMedication();
        pm.setName(medicationName);
        pm.setActive(true);
        pm.setPatient(new Patient(patientId));        
        logger.debug("Saving new patient medication name"+medicationName+" for patientId:"+patientId);
        patientMedicationRepository.save(pm);
        logger.debug("New patient medication saved successfully with id:"+pm.getId());
        return pm;
    }

    /**
     * This method activate or deactivate a patient medication from patient's medication list.
	 * 
     * @param userName a String that represents userName of logged user
     * @param patientId a Long with database patient Id
     * @param medicationId a Long with the database medication Id to be activated again
     * @param state a boolean with true value if the patient medication needs to be activated again and
     * false value if patient medication needs to be deactivated.      
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication
     * @exception a NotFoundException can be thrown if any doctor corresponds with the userName
     */
	public void updateStatePatientMedication(String userName, Long patientId, Long medicationId, boolean sate) {
        logger.info("Calling updateStatePatientMedication for userName:" + userName+ "and patientId:"+patientId+" and medicationId:"+medicationId);
        //load doctor from database for security reasons
        Doctor doctor = getDoctor(userName);
        logger.debug("Searching patient medication with id:"+medicationId);
        PatientMedication pm = patientMedicationRepository.findOne(medicationId);
        if (pm == null){
            throw new NotFoundException("PATIENT_MEDICATION_NOT_FOUND");
        }
        logger.debug("Setting patient medication state with id:"+medicationId);
        pm.setActive(sate);
        patientMedicationRepository.save(pm);
        logger.debug("Patient medication successfully updated with id:"+medicationId);
    }

    /**
     * This method updates the send column to true for every checkin object in the list, 
     * 
     * @param list a List<Checkin>s
     */
	public void updateCheckinSendState(List<Checkin> list) {
		for (Checkin checkin: list){
			checkin.setSend(true);
//			checkinRepository.save(checkin); //we dont need this because all objects are in session and hibernate will update all objects automatically
		}
		
	}

	/**
	 * This method returns a Patient Checkin object 
	 * @param userName a String that represents userName of logged user
	 * @param checkinId a Long with the database checkin Id
	 * @return a Checkin object
	 * @see org.coursera.symptomserver.beans.jpa.Checkin
	 */
	public Checkin getPatientCheckin(String userName, long checkinId) {
		return checkinRepository.findOneByDoctorName(userName, checkinId);
	}
    
}