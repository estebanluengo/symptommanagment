package org.coursera.symptomserver.services;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.coursera.symptomserver.beans.Photo;
import org.coursera.symptomserver.beans.Status;
import org.coursera.symptomserver.beans.jpa.Checkin;
import org.coursera.symptomserver.beans.jpa.CheckinMedication;
import org.coursera.symptomserver.beans.jpa.Patient;
import org.coursera.symptomserver.beans.jpa.PatientMedication;
import org.coursera.symptomserver.exceptions.NotFoundException;
import org.coursera.symptomserver.handlers.SpringSecurityHandler;
import org.coursera.symptomserver.repository.CheckinRepository;
import org.coursera.symptomserver.repository.PatientRepository;
import org.coursera.symptomserver.utils.PhotoFileManager;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service object with business operations for Patients
 */
@Service
@Transactional(rollbackForClassName = "Throwable")
public class PatientService {

    private static final Logger logger = Logger.getLogger(PatientService.class);

    //Object to access Patient table
    @Autowired
    private PatientRepository patientRepository;

    //Object to access Checkin table
    @Autowired
    private CheckinRepository checkinRepository;

    /**
     * Returns a Patient object from database that corresponds to userName 
     * 
     * @param userName a String that represents userName of logged user
     * @return a Patient object.
     * @exception a NotFoundException can be thrown if any patient corresponds with the userName
     * @see org.coursera.symptomserver.beans.Patient
     */
    private Patient getPatient(String userName) {
        Patient patient = patientRepository.findByLogin(userName);
        if (patient == null) {
            logger.debug("Patient not found for userName:" + userName);
            throw new NotFoundException("PATIENT_NOT_FOUND");
        } else {
            logger.debug("Patient found with ID:" + patient.getId());
            return patient;
        }
    }

    /**
     * Returns a status information about patient logged that corresponds to userName.
     *
     * @param userName a String that represents userName of logged user
     * @return a Status object with the patient information, doctor information associated and their medication list
     * @exception a NotFoundException can be thrown if any patient corresponds with the userName
     * @see org.coursera.symptomserver.beans.Status
     */
    public Status getStatus(String userName) {
        logger.info("Calling getStatus for userName:" + userName);
        //Load patient from database
        Patient patient = getPatient(userName);        
        //Load patient medications from database
        logger.debug("loadding patient medications for userName:" + userName);
        patient.getPatientMedicationList().size();
        logger.debug("loadding patient doctor for userName:" + userName);
        patient.getDoctor().getId();     
        //build Status object to return
        Status status = new Status();
        status.setPatient(patient);
        status.setRole(SpringSecurityHandler.PATIENT_ROLE);
        logger.debug("Returning status object for userName:" + userName);
        return status;

    }

    /**
     * Returns patient medications list that corresponds to userName.
     *
     * @param userName a String that represents the userName of the authenticated user
     * @return a List<PatientMedication>
     * @exception a NotFoundException can be thrown if any patient corresponds with the userName
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication
     */
    public List<PatientMedication> getPatientMedications(String userName) {
        logger.info("Calling getPatientMedications for userName:" + userName);
        //Load patient from database
        Patient patient = getPatient(userName);
        //Load patient medications from database
        logger.debug("loadding and returning patient medications for userName:" + userName);
        patient.getPatientMedicationList().size();
        //return list
        return patient.getPatientMedicationList();
    }

    /**
     * This method creates a new check-in with the information send by clients.
     * 
     * @param checkin a Checkin object with check-in information and Check-in medication list. 
     * This check-in medication list may be null.
     * @param userName a String that represents the userName of the authenticated user
     * @return Returns The new Checkin object just inserted in the database
     * @exception a NotFoundException can be thrown if any patient corresponds with the username
     * @see org.coursera.symptomserver.beans.jpa.Checkin
     * @see org.coursera.symptomserver.beans.jpa.CheckinMedication
     */
    public Checkin createCheckin(Checkin checkin, String userName) {
        logger.info("Calling saveCheckin for userName:" + userName);
        //First we inject patient object to check-in and for each PatientMedication object for security reasons        
        Patient patient = getPatient(userName);
        checkin.setId(null); //it's a new check-in. We don't want an Id
        checkin.setPatient(patient);
        checkin.setSend(false); //we have not sent this check-in to doctor yet
        checkin.setAlertDoctor(isNeedsAlertDoctor(checkin, patient.getId()));
        logger.debug("This check-in for username "+userName+" will alert doctor:"+checkin.isAlertDoctor());
        List<CheckinMedication> checkinMedicationList = checkin.getCheckinMedicationList(); 
        if (checkinMedicationList != null){
        	PatientMedication pm;
	        for (CheckinMedication cm : checkinMedicationList) {
	        	cm.setId(null);//it's a new check-in medication. We don't want an Id
	            cm.setCheckin(checkin);
	            pm = cm.getPatientMedication();
	            logger.debug("Injecting patient object to patientMedication object sent by username:"+userName);	            
	            pm.setPatient(patient);
	        }
        }
        Checkin newCheckin = checkinRepository.save(checkin); 
	    logger.debug("New patient checkin saved successfully with id:"+newCheckin.getId());
        
        return newCheckin;
    }

    /**
     * Returns true if a Patient experiences 12 hours of severe pain, 16 hours of "moderate" to "severe pain," or 12 hours of  can not eat
     * 
     * @param lastCheckin check-in that user just upload right now
     * @param patientId database patient id
     * @return a booean with true value if the conditions meets or false if it's not
     */
	private boolean isNeedsAlertDoctor(Checkin lastCheckin, Long patientId) {
		boolean severeOrModerate = true;
		boolean severe = true;
		boolean iCanotEat = true;
		
		DateTime time24 = new DateTime();
		//it needs 24 hours at least to find out if requirements are met
		time24 = time24.minusHours(24);  
		//With only 16 hours of check-in is not enough because maybe the patient didn't make
		//a check-in 16 hours ago and in this case it is necessary to know their last check-in status
		//before 16 hours
		List<Checkin> list = checkinRepository.findAllCheckinsFromTime(patientId, time24.toDate());
		logger.debug("number of checkins 24 hours ago:"+list.size());
		list.add(lastCheckin); //adding last checkin to the list at the end.
		DateTime checkinDate;
		int hours;
		DateTime now = DateTime.now();
		Checkin checkin;
		for (int i=0;i<list.size();i++){
			checkin = list.get(i);
			checkinDate = new DateTime(checkin.getCheckinDate());
			hours = new Period(checkinDate, now).getHours();
			//where are we in the time line?
			if (hours >= 16){
				//saving last severe or moderate check-in
				severeOrModerate = (checkin.getHowbad().equals(Checkin.SEVERE) | 
						checkin.getHowbad().equals(Checkin.MODERATE));
				//saving last I can not eat check-in
				iCanotEat = checkin.getPainstop().equals(Checkin.ICANNOTEAT);
				//saving last severe check-in
				severe = checkin.getHowbad().equals(Checkin.SEVERE);
			}else if (hours >= 12){
				//at this point all checkins 16 hours ago have to be "severe or moderate"
				severeOrModerate = severeOrModerate & (checkin.getHowbad().equals(Checkin.SEVERE) | 
														checkin.getHowbad().equals(Checkin.MODERATE));
				//saving last I can not eat check-in
				iCanotEat = checkin.getPainstop().equals(Checkin.ICANNOTEAT);
				//saving last severe check-in
				severe = checkin.getHowbad().equals(Checkin.SEVERE);
			}else{
				//at this point all checkins 16 hours ago have to be "severe or moderate"
				severeOrModerate = severeOrModerate & (checkin.getHowbad().equals(Checkin.SEVERE) | 
														checkin.getHowbad().equals(Checkin.MODERATE));
				//at this point all checkins 12 hours ago have to be "I can not eat"
				iCanotEat = iCanotEat & checkin.getPainstop().equals(Checkin.ICANNOTEAT);
				//at this point all checkins 12 hours ago have to be "severe" 
				severe = severe & checkin.getHowbad().equals(Checkin.SEVERE);				
			}
//			if (i == 0){
//				iCanotEat = severe = (hours >= 12); //12 hours at least
//				moderate = (hours >= 16); //16 hours at least
//			}
//			if (hours <= 12){ //12 hours or minus
//				severe = severe & checkin.getHowbad().equals(Checkin.SEVERE);
//				iCanotEat = iCanotEat & checkin.getPainstop().equals(Checkin.ICANNOTEAT);
//			}else{
//				moderate = moderate & (checkin.getHowbad().equals(Checkin.SEVERE) | checkin.getHowbad().equals(Checkin.MODERATE));
//			}
		}		
		//true if one of the tree conditions are true
		return severeOrModerate | severe | iCanotEat;	    
	}
	
	/**
     * This method saves a new Patient's check-in photo taken by patient in the file system. 
     * 
     * @param checkinId a Long with database check-in Id
     * @param userName a String that represents the userName of the authenticated user     
     * @param photoData a MultipartFile with photo data
     * @exception a NotFoundException can be thrown if any checkin corresponds to checkinId or
     * if it not possible to save photo in the file system
     * @see org.coursera.symptomserver.beans.jpa.Checkin  
     */
	public void sendPhotoData(Long checkinId, String userName, MultipartFile photoData){
		logger.info("Calling setPhotoData for userName:" + userName);
		PhotoFileManager photoFileManager = null;
		try{
			photoFileManager = PhotoFileManager.get();
		}catch(Exception e){
			throw new NotFoundException("RESOURCE_NOT_FOUND");
		}
		//Does this check-in belong to authenticated user?
		Checkin checkin;
		if ((checkin = checkinRepository.findOneByUserName(userName, checkinId)) == null){
			logger.debug("Checkin not found with id:" + checkinId + " for userName:"+userName);
			throw new NotFoundException("RESOURCE_NOT_FOUND");
		}
		//TODO: save name, size and content-type in checkin object into the database
		try {
			Photo p = new Photo();
			String originalName = photoData.getOriginalFilename();
			logger.debug("Original photo name:" + originalName);
			String extension = "";
			if (originalName != null){
				extension = originalName.substring(originalName.lastIndexOf("."), originalName.length());
			}
			p.setName(originalName);
			p.setFileName(checkinId + extension);
			checkin.setPhotoPath(photoFileManager.savePhotoData(p, photoData.getInputStream()));
			logger.debug("photo saved with name:"+p.getName());
		} catch (IOException e) {
			throw new NotFoundException("RESOURCE_NOT_FOUND");
		}
		
	}
	 
}