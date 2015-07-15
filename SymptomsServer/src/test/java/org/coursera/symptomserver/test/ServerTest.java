package org.coursera.symptomserver.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.coursera.symptomserver.beans.Status;
import org.coursera.symptomserver.beans.jpa.Checkin;
import org.coursera.symptomserver.beans.jpa.CheckinMedication;
import org.coursera.symptomserver.beans.jpa.Patient;
import org.coursera.symptomserver.beans.jpa.PatientMedication;
import org.coursera.symptomserver.client.SecuredRestBuilder;
import org.coursera.symptomserver.client.SymptomSvcApi;
import org.coursera.symptomserver.handlers.SpringSecurityHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;
import retrofit.ErrorHandler;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * JUnit class to test Symptom server
 */
public class ServerTest {
    private final String TEST_URL = "https://localhost:8443";
    private final String CLIENT_ID = "symptom_client";
    private static final String PATIENT_USERNAME = "patient1";
    private static final String PATIENT_PASSWORD = "patient1";
    private static final long PATIENT_ID = 1l;
    private static final String PATIENT_NAME = "Chuck";
    private static final String DOCTOR_USERNAME = "doctor1";
    private static final String DOCTOR_PASSWORD = "doctor1";
    private static final long DOCTOR_ID = 1l;

	private class ErrorRecorder implements ErrorHandler {

		private RetrofitError error;

		@Override
		public Throwable handleError(RetrofitError cause) {
			error = cause;
			return error.getCause();
		}

		public RetrofitError getError() {
			return error;
		}
	}

    
    private File testPhotoData = new File(
			"src/test/resources/photo.jpg");
    
    private JacksonConverter converter = new JacksonConverter(new ObjectMapper());
    
    private ErrorRecorder error = new ErrorRecorder();
    
    private SymptomSvcApi clientPatient = new SecuredRestBuilder()
    		.setConverter(converter)
    		.setClientId(CLIENT_ID)
    		.setUsername(PATIENT_USERNAME)
    		.setPassword(PATIENT_PASSWORD)
            .setClient(new ApacheClient(new EasyHttpClient())) //UnsafeHttpsClient.createUnsafeClient()
            .setErrorHandler(error)
            .setLoginEndpoint(TEST_URL + SymptomSvcApi.TOKEN_PATH)
            .setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
            .create(SymptomSvcApi.class);
    
    private SymptomSvcApi clientDoctor = new SecuredRestBuilder()
			.setConverter(converter)
			.setClientId(CLIENT_ID)
			.setUsername(DOCTOR_USERNAME)
			.setPassword(DOCTOR_PASSWORD)
		    .setClient(new ApacheClient(new EasyHttpClient())) //UnsafeHttpsClient.createUnsafeClient()
		    .setErrorHandler(error)
		    .setLoginEndpoint(TEST_URL + SymptomSvcApi.TOKEN_PATH)
		    .setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		    .create(SymptomSvcApi.class);
    
    private SymptomSvcApi clientUnknown = new SecuredRestBuilder()
		.setConverter(converter)
		.setClientId(CLIENT_ID)
		.setUsername(DOCTOR_USERNAME+"AAA")
		.setPassword(DOCTOR_PASSWORD)
	    .setClient(new ApacheClient(new EasyHttpClient())) //UnsafeHttpsClient.createUnsafeClient()
	    .setErrorHandler(error)
	    .setLoginEndpoint(TEST_URL + SymptomSvcApi.TOKEN_PATH)
	    .setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
	    .create(SymptomSvcApi.class);
    
    public ServerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

     @Test
     public void loginDoctor() {
//         symptomService.login(DOCTOR_USERNAME, DOCTOR_PASSWORD);
         Status status = clientDoctor.status();
         assertTrue(status.getRole().equals(SpringSecurityHandler.DOCTOR_ROLE));
//         symptomService.logout();
     }
     
     @Test
     public void loginPatient() {
//         symptomService.login(PATIENT_USERNAME,PATIENT_PASSWORD);
         Status status = clientPatient.status();
         assertTrue(status.getRole().equals(SpringSecurityHandler.PATIENT_ROLE));
//         symptomService.logout();
     }
     
     @Test
     public void loginUnkown() {         
         try{
//        	 symptomService.login(PATIENT_USERNAME + "A",PATIENT_PASSWORD);
        	 clientUnknown.status();
        	 fail("Yikes, the security setup is horribly broken and didn't require the user to login!!");
         }catch(Exception e){        	 
        	 assertTrue(true);
         }
     }
     
     @Test
     public void getPatientCheckins() {
    	 createCheckin();
//         symptomService.login(DOCTOR_USERNAME,DOCTOR_PASSWORD);
         ArrayList<Checkin> checkinList = clientDoctor.getPatientCheckins(DOCTOR_ID, PATIENT_ID);
         assertTrue(checkinList.size() >= 1);
//         symptomService.logout();
     }
     
     @Test
     public void searchPatientCheckins() {
    	 createCheckin();
//         symptomService.login(DOCTOR_USERNAME,DOCTOR_PASSWORD);
         ArrayList<Checkin> checkinList = clientDoctor.searchPatientCheckins(DOCTOR_ID);
         assertTrue(checkinList.size() >= 1 && checkinList.size() <= 20);
//         symptomService.logout();
     }
     
     @Test
     public void getPatients(){
//    	 symptomService.login(DOCTOR_USERNAME,DOCTOR_PASSWORD);
    	 ArrayList<Patient> patientList = clientDoctor.getPatients(DOCTOR_ID);
    	 assertTrue(patientList.size() == 2);
//    	 symptomService.logout();
     }
     
     @Test
     public void getPatient(){
//    	 symptomService.login(DOCTOR_USERNAME,DOCTOR_PASSWORD);
    	 Patient patient = clientDoctor.getPatient(DOCTOR_ID, PATIENT_NAME);
    	 assertTrue(patient.getName().equals(PATIENT_NAME));
//    	 symptomService.logout();
     }
          
     @Test
     public void createPatientMedication(){
//    	 symptomService.login(DOCTOR_USERNAME,DOCTOR_PASSWORD);
    	 String patientMedicationName = "Med_"+System.currentTimeMillis();
    	 PatientMedication pm = clientDoctor.createPatientMedication(DOCTOR_ID, PATIENT_ID, patientMedicationName);
    	 assertTrue(pm.getId() > 0 && pm.getName().equals(patientMedicationName));
//    	 symptomService.logout();
     }
     
     @Test
     public void deletePatientMedication(){
//    	 symptomService.login(DOCTOR_USERNAME,DOCTOR_PASSWORD);
    	 PatientMedication pm = clientDoctor.createPatientMedication(DOCTOR_ID, PATIENT_ID, "Med_"+System.currentTimeMillis());
    	 Response response = clientDoctor.deletePatientMedication(DOCTOR_ID, PATIENT_ID, pm.getId());    	 
//    	 symptomService.logout();
    	 
//    	 symptomService.login(PATIENT_USERNAME,PATIENT_PASSWORD);
         Status status = clientPatient.status();
         boolean ok = false;
         for (PatientMedication pam: status.getPatient().getPatientMedicationList()){
        	 if (pam.getId() == pm.getId() && !pam.getActive()){
        		 ok = true;
        	 }
         }
//         symptomService.logout();
    	 assertTrue((response.getStatus() == HttpStatus.SC_OK) && ok);    	 
     }
     
     @Test
     public void activatePatientMedication(){
//    	 symptomService.login(DOCTOR_USERNAME,DOCTOR_PASSWORD);
    	 PatientMedication pm = clientDoctor.createPatientMedication(DOCTOR_ID, PATIENT_ID, "Med_"+System.currentTimeMillis());
    	 clientDoctor.deletePatientMedication(DOCTOR_ID, PATIENT_ID, pm.getId());
    	 Response response = clientDoctor.activatePatientMedication(DOCTOR_ID, PATIENT_ID, pm.getId());
//    	 symptomService.logout();
    	 
//    	 symptomService.login(PATIENT_USERNAME,PATIENT_PASSWORD);
         Status status = clientPatient.status();
         boolean ok = false;
         for (PatientMedication pam: status.getPatient().getPatientMedicationList()){
        	 if (pam.getId() == pm.getId() && pam.getActive()){
        		 ok = true;
        	 }
         }
//         symptomService.logout();
    	 assertTrue((response.getStatus() == HttpStatus.SC_OK) && ok);    	 
     }
     
     @Test
     public void createCheckin(){    	    	 
//    	 symptomService.login(PATIENT_USERNAME,PATIENT_PASSWORD);    	 
         Status status = clientPatient.status();
    	 ArrayList<PatientMedication> patientMedicationList = (ArrayList<PatientMedication>) status.getPatient().getPatientMedicationList();
    	     	 
    	 Checkin serverCheckin = clientPatient.createCheckin(PATIENT_ID, createCheckin(patientMedicationList));
    	 assertTrue(serverCheckin.getId() > 0);
//    	 symptomService.logout();
     }
     
     @Test
     public void sendCheckinPhoto(){
//    	 symptomService.login(PATIENT_USERNAME,PATIENT_PASSWORD);    	 
         Status status = clientPatient.status();
    	 ArrayList<PatientMedication> patientMedicationList = (ArrayList<PatientMedication>) status.getPatient().getPatientMedicationList();    	     	 
    	 Checkin serverCheckin = clientPatient.createCheckin(PATIENT_ID, createCheckin(patientMedicationList));      	 
    	 
    	 Response response = clientPatient.sendCheckinPhoto(PATIENT_ID, serverCheckin.getId(), new TypedFile("image/jpg", testPhotoData));
    	 assertEquals(200, response.getStatus());
//    	 symptomService.logout();
     }
     
     @Test
     public void getCheckinPhoto() throws Exception{
    	 Checkin checkin = sendPhoto();
//    	 symptomService.login(DOCTOR_USERNAME, DOCTOR_PASSWORD);
    	 Response response = clientDoctor.getCheckinPhoto(DOCTOR_ID, PATIENT_ID, checkin.getId());
    	 assertEquals(200, response.getStatus());
    	 InputStream photoData = response.getBody().in();
 		 byte[] originalFile = IOUtils.toByteArray(new FileInputStream(testPhotoData));
 		 byte[] retrievedFile = IOUtils.toByteArray(photoData);
 		 assertTrue(Arrays.equals(originalFile, retrievedFile));
//    	 symptomService.logout();
     }
     
     
     /**
      * 
      * @return
      */
     private Checkin sendPhoto(){
//    	 symptomService.login(PATIENT_USERNAME,PATIENT_PASSWORD);    	 
         Status status = clientPatient.status();
    	 ArrayList<PatientMedication> patientMedicationList = (ArrayList<PatientMedication>) status.getPatient().getPatientMedicationList();    	     	 
    	 Checkin serverCheckin = clientPatient.createCheckin(PATIENT_ID, createCheckin(patientMedicationList));      	     	 
    	 clientPatient.sendCheckinPhoto(PATIENT_ID, serverCheckin.getId(), new TypedFile("image/jpg", testPhotoData));
//    	 symptomService.logout();
    	 return serverCheckin;
     }
     
     /**
      * 
      * @param patientMedicationList
      * @return
      */
     private Checkin createCheckin(ArrayList<PatientMedication> patientMedicationList){
    	 Checkin checkin = new Checkin();
    	 checkin.setCheckinDate(new Date());
    	 checkin.setHowbad("Severe");
    	 checkin.setPainstop("No");
    	 checkin.setPatient(new Patient(PATIENT_ID));
    	 ArrayList<CheckinMedication> checkinMedicationList = new ArrayList<CheckinMedication>();
    	 for (PatientMedication pm: patientMedicationList){
	    	 CheckinMedication checkinMedication = new CheckinMedication();
	    	 checkinMedication.setTakeit(true);
	    	 checkinMedication.setTakeitDate("2014-11-21");
	    	 checkinMedication.setTakeitTime("10:20");
	    	 checkinMedication.setPatientMedication(pm);
	    	 checkinMedicationList.add(checkinMedication);
    	 }
    	 checkin.setCheckinMedicationList(checkinMedicationList);
    	 return checkin;
     }

}
