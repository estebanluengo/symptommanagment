package org.coursera.symptom.client;

import java.util.ArrayList;

import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.orm.Patient;
import org.coursera.symptom.orm.PatientMedication;
import org.coursera.symptom.orm.Status;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * The REST API for the Symptoms Management Server that all clients must use to access to the system
 */
public interface SymptomSvcApi {
    
	public static final String TOKEN_PATH = "/oauth/token";
    public static final String PASSWORD_PARAMETER = "password";
    public static final String USERNAME_PARAMETER = "username";
    public static final String PATIENT_NAME = "patientName";
    public static final String PATIENT_MEDICATION_NAME = "medicationName";
    public static final String DATE_FROM = "dateFrom";
    public static final String PATIENT_ID = "patient_Id";
    public static final String DOCTOR_ID = "doctor_id";
    public static final String CHECKIN_ID = "checkin_id";
    public static final String PATIENT_MEDICATION_ID = "medication_id";
    public static final String DATA_PARAMETER = "data";

    public static final String LOGIN_PATH = "/login";
    public static final String LOGOUT_PATH = "/logout";    
    public static final String BASE_PATH = "/symptom";
    
    public static final String DOCTORS_BASE_PATH = BASE_PATH + "/doctors";  
    public static final String PATIENTS_BASE_PATH = BASE_PATH + "/patients";
    public static final String STATUS_PATH = BASE_PATH + "/getstatus";
    public static final String PATIENT_CHECKINS_PATH = BASE_PATH + "/patients/{" + PATIENT_ID + 
    		"}/checkins";
    public static final String PATIENT_MEDICATION_PATH = BASE_PATH + "/patients/{" + PATIENT_ID + 
    		"}/medications";
    public static final String DOCTOR_PATIENT_CHECKINS_PATH = BASE_PATH + "/doctors/{" + DOCTOR_ID + 
    		"}/patients/{" + PATIENT_ID + "}/checkins";
    public static final String DOCTOR_PATIENTS_PATH = BASE_PATH + "/doctors/{" + DOCTOR_ID + 
    		"}/patients";
    public static final String DOCTOR_SEARCHPATIENT_PATH = BASE_PATH + "/doctors/{" + DOCTOR_ID + 
    		"}/patient/search";    
    public static final String DOCTOR_SEARCHCHECKINS_PATH = BASE_PATH + "/doctors/{" + DOCTOR_ID + 
    		"}/checkins/search";
    public static final String DOCTOR_CREATE_PATIENT_MEDICATIONS_PATH = BASE_PATH + "/doctors/{" + 
    		DOCTOR_ID + "}/patients/{" + PATIENT_ID + "}/medications";
    public static final String DOCTOR_DELETE_PATIENT_MEDICATIONS_PATH = BASE_PATH + "/doctors/{" + 
    		DOCTOR_ID + "}/patients/{" + PATIENT_ID + "}/medications/{" + PATIENT_MEDICATION_ID +"}";
    public static final String PATIENT_CHECKINS_DATA_PATH = BASE_PATH + "/patients/{" + PATIENT_ID + 
    		"}/checkins/{" + CHECKIN_ID + "}/data";
    public static final String DOCTOR_PATIENT_CHECKIN_DATA_PATH = BASE_PATH + "/doctors/{" + 
    		DOCTOR_ID + "}/patients/{" + PATIENT_ID + "}/checkins/{" + CHECKIN_ID + "}/data";
    
    /**
     * This method is deprecated now because the server uses OAUTH authentication system
     * @param username the username for login
     * @param pass the password for login
     * @deprecated
     */
    @FormUrlEncoded
    @POST(LOGIN_PATH)
    public Void login(@Field(USERNAME_PARAMETER) String username, @Field(PASSWORD_PARAMETER) String pass);

    /**
     * This method is deprecated now because the server uses OAUTH authentication system
     * @deprecated
     */
    @GET(LOGOUT_PATH)
    public Void logout();

    /**
     * This method returns user status information to indicate them who are their. 
     * Both Patients and doctors can call this method
     * 
     * @return a Status object
     * @see org.coursera.symptomserver.beans.Status
     */
    @GET(STATUS_PATH)
    public Status status();
        
    /**
     * This method returns all patient's medications list. Only Patients can call this method.
     * 
     * @param patientId a Long with database patient Id
     * @return an ArrayList<PatientMedication>
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication
     */
    @GET(PATIENT_MEDICATION_PATH)
    public ArrayList<PatientMedication> getPatientMedications(@Path(PATIENT_ID) Long patientId);
    
    /**
     * This method creates a new Patient's check-in information. Only Patients can call this method.
     * 
     * @param patientId a Long with database patient Id
     * @param checkin a Checkin object with Checkin data and the CheckinMedication list information associated to Checkin
     * @return the same Checkin object but with the checkin Id used to saved it in the database
     * @see org.coursera.symptomserver.beans.jpa.Checkin 
     */
    @POST(PATIENT_CHECKINS_PATH)
    public Checkin createCheckin(@Path(PATIENT_ID) Long patientId, @Body Checkin checkin);
    
    /**
     * This method returns Doctor's patient list. Only Doctors can call this method.
     * 
     * @param doctorId a Long with database doctor Id
     * @return an ArrayList<Patient>. Nor Patient's checkin information neither Patient's medication is returned.
     * @see org.coursera.symptomserver.beans.jpa.Patient
     */
    @GET(DOCTOR_PATIENTS_PATH)
    public ArrayList<Patient> getPatients(@Path(DOCTOR_ID) Long doctorId);
        
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
    @GET(DOCTOR_PATIENT_CHECKINS_PATH)
    public ArrayList<Checkin> getPatientCheckins(@Path(DOCTOR_ID) Long doctorId, @Path(PATIENT_ID) Long patientId);
    
    /**
     * This method returns the new doctor patients' checkins information. Only Doctors can call this method.
     * Once the checkins are returned, they will never be returned again.
     * 
     * @param doctorId a Long with database doctor Id
     * @return an ArrayList<Checkin>. Every Checkin object contains CheckinMedication list
     * @see org.coursera.symptomserver.beans.jpa.Checkin 
     */
    @GET(DOCTOR_SEARCHCHECKINS_PATH)
    public ArrayList<Checkin> searchPatientCheckins(@Path(DOCTOR_ID) Long doctorId);
    
    /**
     * This method returns a Patient information that corresponds with patient name send it.
     * Only Doctors can call this method.
     * 
     * @param doctorId a Long with database doctor Id
     * @param patientName a String with patient name
     * 
     * @return a Patient object. Nor Patient's checkin information neither Patient's medication is returned.
     * @see org.coursera.symptomserver.beans.jpa.Patient
     */
    @GET(DOCTOR_SEARCHPATIENT_PATH)
    public Patient getPatient(@Path(DOCTOR_ID) Long doctorId, @Query(PATIENT_NAME) String patientName);
    
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
    @POST(DOCTOR_CREATE_PATIENT_MEDICATIONS_PATH)
    public PatientMedication createPatientMedication(@Path(DOCTOR_ID) Long doctorId, @Path(PATIENT_ID) Long patientId, 
    		@Query(PATIENT_MEDICATION_NAME) String medicationName);
    
    /**
     * This method deletes a patient medication from patient's medication list. Only Doctors can call this method.
     * The method does not delete any patient's medication, it just deactivate from patient's medication list
     * 
     * @param doctorId a Long with database doctor Id
     * @param patientId a Long with database patient Id
     * @param medicationId a Long with the database medication Id to be deleted.
     * 
     * @return a Response object with HttpStatus.OK 
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication 
     */
    @DELETE(DOCTOR_DELETE_PATIENT_MEDICATIONS_PATH)
    public Response deletePatientMedication(@Path(DOCTOR_ID) Long doctorId, @Path(PATIENT_ID) Long patientId, 
    		@Path(PATIENT_MEDICATION_ID) Long medicationId);
    
    /**
     * This method activate a patient medication from patient's medication list. Only Doctors can call this method.
     * 
     * @param doctorId a Long with database doctor Id
     * @param patientId a Long with database patient Id
     * @param medicationId a Long with the database medication Id to be activated again
     * 
     * @return a Response object with HttpStatus.OK 
     * @see org.coursera.symptomserver.beans.jpa.PatientMedication 
     */
    @PUT(DOCTOR_DELETE_PATIENT_MEDICATIONS_PATH)
    public Response activatePatientMedication(@Path(DOCTOR_ID) Long doctorId, @Path(PATIENT_ID) Long patientId, 
    		@Path(PATIENT_MEDICATION_ID) Long medicationId);
    
    /**
     * This method sends a new Patient's check-in photo taken by patient. Only Doctors can call this method.
     *  
     * @param patientId a Long with database patient Id
     * @param checkinId a Long with database check-in Id
     * @param photoData a MultipartFile object with photo information
     * 
     * @return a Response object with HttpStatus.OK
     * @see org.coursera.symptomserver.beans.jpa.Checkin  
     */
    @Multipart
    @POST(PATIENT_CHECKINS_DATA_PATH)
    public Response sendCheckinPhoto(@Path(PATIENT_ID) Long patientId, @Path(CHECKIN_ID) Long checkinId, 
    		@Part(DATA_PARAMETER) TypedFile photoData);
    
    /**
     * This method returns the Patient's check-in photo taken by patient. Only Doctors can call this method.
     * 
     * @param patientId a Long with database patient Id
     * @param checkinId a Long with database check-in Id
     * @param photoData a TypedFile with photo information
     * 
     * @return a Response object with HttpStatus.OK 
     * @see org.coursera.symptomserver.beans.jpa.Checkin   
     */
    @Streaming
    @GET(DOCTOR_PATIENT_CHECKIN_DATA_PATH)
    public Response getCheckinPhoto(@Path(DOCTOR_ID) Long doctorId, @Path(PATIENT_ID) Long patientId, 
    		@Path(CHECKIN_ID) Long checkinId);
}