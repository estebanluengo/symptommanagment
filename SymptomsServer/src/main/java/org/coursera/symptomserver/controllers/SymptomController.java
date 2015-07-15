package org.coursera.symptomserver.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.coursera.symptomserver.beans.Status;
import org.coursera.symptomserver.beans.jpa.Checkin;
import org.coursera.symptomserver.beans.jpa.Patient;
import org.coursera.symptomserver.beans.jpa.PatientMedication;
import org.coursera.symptomserver.exceptions.AccessException;
import org.coursera.symptomserver.exceptions.NotFoundException;
import org.coursera.symptomserver.handlers.SpringSecurityHandler;
import org.coursera.symptomserver.services.DoctorService;
import org.coursera.symptomserver.services.PatientService;
import org.coursera.symptomserver.utils.PhotoFileManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller class that implements the API REST for mobile apps
 */
@RestController
@RequestMapping("/symptom")
public class SymptomController {
    
    private static final Logger logger = Logger.getLogger(SymptomController.class);
    
    //This object helps to find out what user called theses methods
    @Autowired    
    private SpringSecurityHandler securityHandler;
    
    //Doctor's business
    @Autowired
    private DoctorService doctorService;
    
    //Patient's business
    @Autowired
    private PatientService patientService;
    
    /**
     * This method returns user status information to indicate them who are their
     * Both Patients and doctors can call this method.
     * 
     * @return a Status object
     * @see org.coursera.symptomserver.beans.Status
     * @exception an AccessException can be thrown if user's role is not a Patient nor a Doctor 
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getstatus", produces = {"application/json"})
    public Status getStatus(){
    	
        logger.info("Calling getStatus");
        String userRole = securityHandler.getUserRole();        
        if (SpringSecurityHandler.UNKNOWN_ROLE.equals(userRole)){
            throw new AccessException("UNAUTHORIZED_ACCESS");
        }
        String userName = securityHandler.getUserName();
        if (SpringSecurityHandler.DOCTOR_ROLE.equals(userRole)){
            return doctorService.getStatus(userName);
        }else{
            Status status = patientService.getStatus(userName);
            status.getPatient().getDoctor().setLogin("");
            return status;
        }                            
    }
    
    /**
     * This method returns all patient's medications list. Only Patients can call this method.
     * 
     * @param patientId a Long with database patient Id
     * @return an ArrayList<PatientMedication>
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication
     */
    @RequestMapping(method = RequestMethod.GET, value = "/patients/{patient_id}/medications", produces = {"application/json"})
    public List<PatientMedication> getPatientMedications(@PathVariable("patient_id") Long patientId){
    	
        logger.info("Calling getPatientMedication for patient_id:"+patientId);        
        String userName = securityHandler.getUserName();
        return patientService.getPatientMedications(userName);
    }
    
    /**
     * This method creates a new Patient's check-in information. Only Patients can call this method.
     * 
     * @param patientId a Long with database patient Id
     * @param checkin a Checkin object with Checkin data and the CheckinMedication list information associated to Checkin
     * @return the same Checkin object but with the checkin Id used to saved it in the database
     * @see org.coursera.symptomserver.beans.jpa.Checkin 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/patients/{patient_id}/checkins", produces = {"application/json"})
    public Checkin createCheckin(@PathVariable("patient_id") Long patientId, @RequestBody Checkin checkin){
    	
        logger.info("Calling getPatientMedication for patient_id:"+patientId);
        String userName = securityHandler.getUserName();
        return patientService.createCheckin(checkin, userName);
    }
    
    /**
     * This method returns Doctor's patient list. Only Doctors can call this method.
     * 
     * @param doctorId a Long with database doctor Id
     * @return an ArrayList<Patient>. Nor Patient's checkin information neither Patient's medication is returned.
     * @see org.coursera.symptomserver.beans.jpa.Patient
     */
    @RequestMapping(method = RequestMethod.GET, value = "/doctors/{doctor_id}/patients", produces = {"application/json"})
    public List<Patient> getPatients(@PathVariable("doctor_id") Long doctorId){
    	
        logger.info("Calling getPatients for doctor_id:"+doctorId);        
        String userName = securityHandler.getUserName();
        return doctorService.getPatients(userName);
    }
    
    /**
     * This method returns a Patient information that corresponds to patientName.
     * Only Doctors can call this method.
     * 
     * @param doctorId a Long with database doctor Id
     * @param patientName a String with patient name
     * 
     * @return a Patient object. Nor Patient's checkin information neither Patient's medication is returned.
     * @see org.coursera.symptomserver.beans.jpa.Patient
     */
    @RequestMapping(method = RequestMethod.GET, value = "/doctors/{doctor_id}/patient/search", produces = {"application/json"})
    public Patient getPatient(@PathVariable("doctor_id") Long doctorId,
    		@RequestParam(value = "patientName", required = true) String patientName){
    	
    	logger.info("Calling getPatient for doctor_id:"+doctorId);        
        String userName = securityHandler.getUserName();
        return doctorService.getPatient(userName, patientName);
    }
    
    /**
     * This method returns a patient's check-in list. Only Doctors can call this method.
     * The method only returns the last MAX_CHECKIN_RESULTS check-ins defined at DoctorService class
     * 
     * @param doctorId a Long with database doctor Id
     * @param patientId a Long with database patient Id
     * @return an ArrayList<Checkin>. Every Checkin object contains CheckinMedication list 
     * @see org.coursera.symptomserver.beans.jpa.Checkin
     * @see org.coursera.symptomserver.services.DoctorService
     */
    @RequestMapping(method = RequestMethod.GET, value = "/doctors/{doctor_id}/patients/{patient_id}/checkins", produces = {"application/json"})
    public List<Checkin> getPatientCheckins(@PathVariable("doctor_id") Long doctorId, @PathVariable("patient_id") Long patientId){
    	
        logger.info("Calling getPatientCheckins for doctor_id:"+doctorId+" and patinetId:"+patientId);        
        String userName = securityHandler.getUserName();
        return doctorService.getPatientCheckins(userName, patientId);
    }
    
    /**
     * This method returns the new doctor patients' checkins information. Only Doctors can call this method.
     * Once the checkins are returned, they will never be returned again.
     * 
     * @param doctorId a Long with database doctor Id
     * @return an ArrayList<Checkin>. Every Checkin object contains CheckinMedication list
     * @see org.coursera.symptomserver.beans.jpa.Checkin 
     */
    @RequestMapping(method = RequestMethod.GET, value = "/doctors/{doctor_id}/checkins/search", produces = {"application/json"})
    public List<Checkin> searchPatientCheckins(@PathVariable("doctor_id") Long doctorId, 
            @RequestParam(value = "patientName", required = false) String patientName,
            @RequestParam(value = "dateFrom", required = false) String dateFrom){
    	
        logger.info("Calling getPatientCheckins for doctor_id:"+doctorId+" and patinet name:"+patientName+" from:"+dateFrom);        
        String userName = securityHandler.getUserName();
        Date df = null;
        if (dateFrom != null){
            logger.debug("Converting date "+dateFrom+" from String to Date object");
            DateTime date = new DateTime(dateFrom);        
            df = date.toDate();
        }
        List<Checkin> list = doctorService.getPatientCheckins(userName, patientName, df);
        if (dateFrom == null && patientName == null){
        	logger.debug("Updating send state of every checkin");
        	doctorService.updateCheckinSendState(list);
        }
        return list;
    }
    
    /**
     * This method creates new Patient medication information. Only Doctors can call this method.
     * 
     * @param doctorId a Long with database doctor Id
     * @param patientId a Long with database patient Id
     * @param medicationName a String with the medication name to be created for patient.
     * 
     * @return the new PatientMedication object created with the database Id.
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication
     */
    @RequestMapping(method = RequestMethod.POST, value = "/doctors/{doctor_id}/patients/{patient_id}/medications", produces = {"application/json"})
    public PatientMedication createPatientMedication(@PathVariable("doctor_id") Long doctorId, @PathVariable("patient_id") Long patientId, 
            @RequestParam("medicationName") String medicationName){
    	
        logger.info("Calling createPatientMedication for doctor_id:"+doctorId+" and medication name:"+medicationName);
        String userName = securityHandler.getUserName();
        return doctorService.createPatientMedication(userName, patientId, medicationName);
    }
    
    /**
     * This method activate a patient medication from patient's medication list.
     * Only Doctors can call this method.
     * @param doctorId a Long with database doctor Id
     * @param patientId a Long with database patient Id
     * @param medicationId a Long with the database medication Id to be activated again
     * 
     * @return a Response object with HttpStatus.OK 
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication 
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/doctors/{doctor_id}/patients/{patient_id}/medications/{medication_id}", produces = {"application/json"})
    public ResponseEntity<String> activatePatientMedication(@PathVariable("doctor_id") Long doctorId, @PathVariable("patient_id") Long patientId, 
            @PathVariable("medication_id") Long medicationId){
    	
        logger.info("Calling activatePatientMedication for doctor_id:"+doctorId+" and medication id:"+medicationId);
        String userName = securityHandler.getUserName();
        doctorService.updateStatePatientMedication(userName, patientId, medicationId, true);
        return new ResponseEntity<>(HttpStatus.OK);
    }
        
    /**
     * This method deletes a patient medication from patient's medication list.
     * Only Doctors can call this method. 
     * The method does not delete any patient's medication, it just deactivate from patient's medication list
     * 
     * @param doctorId a Long with database doctor Id
     * @param patientId a Long with database patient Id
     * @param medicationId a Long with the database medication Id to be deleted.
     * 
     * @return a Response object with HttpStatus.OK 
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication 
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/doctors/{doctor_id}/patients/{patient_id}/medications/{medication_id}", produces = {"application/json"})
    public ResponseEntity<String> deletePatientMedication(@PathVariable("doctor_id") Long doctorId, @PathVariable("patient_id") Long patientId, 
            @PathVariable("medication_id") Long medicationId){
    	
        logger.info("Calling deletePatientMedication for doctor_id:"+doctorId+" and medication id:"+medicationId);
        String userName = securityHandler.getUserName();
        doctorService.updateStatePatientMedication(userName, patientId, medicationId, false);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * This method sends a new Patient's check-in photo taken by patient. Only Doctors can call this method.
     * 
     * @param patientId a Long with database patient Id
     * @param checkinId a Long with database check-in Id
     * @param photoData a TypedFile with photo information
     * 
     * @return a Response object with HttpStatus.OK
     * @see org.coursera.symptomserver.beans.jpa.Checkin  
     */
    @RequestMapping(value = "/patients/{patient_id}/checkins/{checkin_id}/data", method = RequestMethod.POST)
	public ResponseEntity<String> sendPhotoData(@PathVariable("patient_id") long patientId, @PathVariable("checkin_id") long checkinId,
			@RequestParam("data") MultipartFile photoData) {
    	
    	logger.info("Calling setPhotoData for patient_id:"+patientId);       	
        String userName = securityHandler.getUserName();
        patientService.sendPhotoData(checkinId, userName, photoData);
        return new ResponseEntity<>(HttpStatus.OK);
	}
    
    /**
     * This method returns the Patient's check-in photo taken by patient. Only Doctors can call this method.
     *  
     * @param patientId a Long with database patient Id
     * @param checkinId a Long with database check-in Id
     * @param photoData a TypedFile with photo information
     * 
     * @return a Response object with HttpStatus.OK if photo is present or HttpStatus.NOT_FOUND if photo is not present. 
     * @see org.coursera.symptomserver.beans.jpa.Checkin   
     */
    @RequestMapping(value = "/doctors/{doctor_id}/patients/{patient_id}/checkins/{checkin_id}/data", method = RequestMethod.GET)
	public void getPhotoData(@PathVariable("doctor_id") long doctorId, @PathVariable("patient_id") long patientId, 
			@PathVariable("checkin_id") long checkinId, HttpServletResponse response){
    	
    	logger.info("Calling getPhotoData for patient_id:"+patientId);
    	PhotoFileManager photoFileManager = null;
    	try {
			photoFileManager = PhotoFileManager.get();
		} catch (IOException e) {
			throw new NotFoundException("RESOURCE_NOT_FOUND");
		}
    	String userName = securityHandler.getUserName();
    	Checkin checkin = doctorService.getPatientCheckin(userName, checkinId);
    	if (checkin == null){
    		logger.debug("checkin not found with id:"+checkinId+" for doctor:"+userName);
    		throw new NotFoundException("RESOURCE_NOT_FOUND");
    	}
    	String picurePath = checkin.getPhotoPath();
    	if (photoFileManager.hasPhotoData(picurePath)){
    		logger.debug("starting to send photo to client with name:"+picurePath);
    		try {
				photoFileManager.copyPhotoData(picurePath, response.getOutputStream());
				logger.debug("Photo sent to server");
			} catch (IOException e) {
				logger.debug("it was an error sending photo with name:"+picurePath, e);
	    		throw new NotFoundException("RESOURCE_NOT_FOUND");
			}
    	}else{
    		logger.debug("photo not found with name:"+picurePath);
    		throw new NotFoundException("RESOURCE_NOT_FOUND");
    	}
    } 
}
