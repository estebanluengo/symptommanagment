package org.coursera.symptom.orm;

import java.util.ArrayList;

import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.provider.SymptomSchema;
import org.coursera.symptom.utils.Utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * encapsulation of the ContentResolver for a single URI
 * <p>
 * Uses ContentResolver instead of ContentProviderClient or other mechanism to
 * simplify code and to make this object thread safe. Future revisions to the
 * code could have ContentProviderClient (which takes 50% of the time during
 * access, once setup, than ContentResolver( did independent testing to find
 * this out.))
 * 
 * @author Michael A. Walker
 * @author me
 * 
 */
public class SymptomResolver {

	public static final String TAG = "SymptomResolver";
	private ContentResolver cr;

	private Uri doctorURI = SymptomSchema.Doctor.CONTENT_URI;
	private Uri patientURI = SymptomSchema.Patient.CONTENT_URI;
	private Uri checkinURI = SymptomSchema.Checkin.CONTENT_URI;
	private Uri patientMedicationURI = SymptomSchema.PatientMedication.CONTENT_URI;
	private Uri checkinMedicationURI = SymptomSchema.CheckinMedication.CONTENT_URI;
	private Uri statusURI = SymptomSchema.Status.CONTENT_URI;
	

	/**
	 * Constructor
	 * 
	 * @param activity
	 *            The Activity to get the ContentResolver from.
	 */
	public SymptomResolver(Context context) {
		cr = context.getContentResolver();
	}

	/**
	 * ApplyBatch, simple pass-through to the ContentResolver implementation.
	 * 
	 * @param operations
	 * @return array of ContentProviderResult
	 * @throws RemoteException
	 * @throws OperationApplicationException
	 */
	public ContentProviderResult[] applyBatch(final ArrayList<ContentProviderOperation> operations) throws RemoteException, OperationApplicationException {
		return cr.applyBatch(SymptomSchema.AUTHORITY, operations);
	}
	
	/**
	 * Returns what is the user's role searching into the database. Maybe we will not know
	 * who is the user because is the first time the user uses this app.
	 * 
	 * @param serverStatus a Status object that contains user information recover from the server
	 * @return a String with three possible values: PATIENT, ROLE or UNKNOWN
	 * @throws RemoteException in case of error the method will throw a RemoteException
	 */
	public Status whoAmI(Status serverStatus) throws RemoteException{
		Log.d(TAG, "calling whoAmI()");
		Status status = getStatus(); //local user information
		if (status != null){ //it exits previous user information in the local database
			long serverId = -1;
			long localId = status.getId();
			//same role server user role than local user role
			if (SymptomValues.ROLE_DOCTOR.equals(serverStatus.getRole()) &&
				SymptomValues.ROLE_DOCTOR.equals(status.getRole())){				
				serverId = serverStatus.getDoctor().getId(); 
			}
			if (SymptomValues.ROLE_PATIENT.equals(serverStatus.getRole()) &&
				SymptomValues.ROLE_PATIENT.equals(status.getRole())){				
				serverId = serverStatus.getPatient().getId(); 
			}
			if (serverId != localId){ //different Ids
				deleteAllInformation();
				return null;//A new user. Delete all information to prevent access violation to medical data
			}						
		}
		return status; //if we are here the user recover from server is the same than local user information		
	}	
	
	/**
	 * Delete all.
	 * 
	 * @throws RemoteException 
	 */
	public void deleteAllInformation() throws RemoteException{
		deleteStatus(null, null);
		deleteDoctor(null, null);
		deletePatient(null, null);
		deleteCheckinMedication(null, null);
		deleteCheckin(null, null);
		deletePatientMedication(null, null);
	}

	/*
	 * Bulk Insert for each ORM Data Type
	 */
	
	/**
	 * Insert a group of Checkin into the databse 
	 * 
	 * @param data ArrayList of Checkin objects to be inserted into the database
	 * @throws RemoteException
	 */
	public void bulkInsertCheckin(final ArrayList<Checkin> data) throws RemoteException {
		Log.d(TAG, "Calling bulkInsertCheckin()");
		for (Checkin checkin : data) {
			insert(checkin);
		}
	}

	/**
	 * Insert a group of PatientMedication all at once. Mainly useful for use on
	 * installation/first boot of an application. Allowing setup of the Database
	 * into a 'start state'
	 * 
	 * @param data
	 * @return
	 * @throws RemoteException
	 */
	public int bulkInsertPatientMedication(final ArrayList<PatientMedication> data, Patient patient) throws RemoteException {
		Log.d(TAG, "Calling bulkInsertPatientMedication()");
		ContentValues[] values = new ContentValues[data.size()];
		int index = 0;
		for (PatientMedication patientMedication : data) {
			patientMedication.setPatient(patient);
			values[index] = patientMedication.getContentValues();
			++index;
		}
		return cr.bulkInsert(patientMedicationURI, values);
	}
	
	/**
	 * Insert a group of CheckinMedication all at once. Mainly useful for use on
	 * installation/first boot of an application. Allowing setup of the Database
	 * into a 'start state'
	 * 
	 * @param data
	 * @return
	 * @throws RemoteException
	 */
	public int bulkInsertCheckinMedication(final ArrayList<CheckinMedication> data) throws RemoteException {
		ContentValues[] values = new ContentValues[data.size()];
		int index = 0;
		for (CheckinMedication checkinMedication : data) {
			values[index] = checkinMedication.getContentValues();
			++index;
		}
		return cr.bulkInsert(checkinMedicationURI, values);
	}	
		
	/*
	 * Insert for each ORM Data Type
	 */

	/**
	 * Insert a new Doctor object into the ContentProvider
	 * 
	 * @param doctor Doctor to be inserted
	 * @return URI of inserted Doctor in the ContentProvider
	 * @throws RemoteException
	 */
	public Uri insert(final Doctor doctor) throws RemoteException {
		Log.d(TAG, "Calling insert doctor()");
		ContentValues tempCV = doctor.getContentValues();
//		tempCV.remove(SymptomSchema.Doctor.Cols.ID);
		Uri uri = cr.insert(doctorURI, tempCV);
		Log.d(TAG, "Doctor inserted succesfully with uri:"+uri);
		return uri;
	}

	public Uri insert(final Patient patient) throws RemoteException {
		Log.d(TAG, "Calling insert patient()");
		Doctor doctor = patient.getDoctor();
		Uri uriDoctor = insert(doctor);
		Log.d(TAG, "Doctor inserted succesfully with uri:"+uriDoctor);
		doctor.setId((long)ContentUris.parseId(uriDoctor));
		
		ContentValues tempCV = patient.getContentValues();
//		tempCV.remove(SymptomSchema.Patient.Cols.ID);
		Uri uriPatient = cr.insert(patientURI, tempCV);	
		Log.d(TAG, "Patient inserted succesfully with uri:"+uriPatient);
		
		patient.setId((long)ContentUris.parseId(uriPatient));
		bulkInsertPatientMedication((ArrayList<PatientMedication>) patient.getPatientMedicationList(), patient);
		Log.d(TAG, "Patient's medication inserted succesfully");		
		return uriPatient;
	}	
	
	public Uri insert(final Checkin checkin) throws RemoteException {
		Log.d(TAG, "Calling insert checkin()");
		ContentValues tempCV = checkin.getContentValues();
//		tempCV.remove(SymptomSchema.Checkin.Cols.ID);
		Uri uriCheckin =  cr.insert(checkinURI, tempCV);
		Log.d(TAG, "Checkin inserted succesfully with uri:"+uriCheckin);
		checkin.setId((long)ContentUris.parseId(uriCheckin));
		if (checkin.getCheckinMedicationList() != null){
			for (CheckinMedication cm: checkin.getCheckinMedicationList()){
				cm.setCheckin(checkin);
				Uri uriCheckinMedication = insert(cm);
				Log.d(TAG, "Checkin medication inserted succesfully with uri:"+uriCheckinMedication);
			}
		}else{
			Log.d(TAG, "This check-in does not have checkin medication");
		}
		return uriCheckin;
	}	
	
	public Uri insert(final CheckinMedication checkinMedication) throws RemoteException {
		Log.d(TAG, "Calling insert checkinMedication()");
		ContentValues tempCV = checkinMedication.getContentValues();
//		tempCV.remove(SymptomSchema.CheckinMedication.Cols.ID); 
		return cr.insert(checkinMedicationURI, tempCV);
	}	
	
	public Uri insert(final PatientMedication patientMedication) throws RemoteException {		
		ContentValues tempCV = patientMedication.getContentValues();
//		tempCV.remove(SymptomSchema.PatientMedication.Cols.ID);
		return cr.insert(patientMedicationURI, tempCV);
	}	
	
	public Uri insert(final Status status) throws RemoteException {
		Log.d(TAG, "Calling insert status()");
		status.setId(); //Fixing id attribute to save this object into the database
		status.setDateInsert(Utils.getCurrentDate());
		ContentValues tempCV = status.getContentValues();
		Uri uriStatus = cr.insert(statusURI, tempCV);
		Log.d(TAG, "Status inserted succesfully with uri:"+uriStatus);
		return uriStatus;
	}

	/*
	 * Delete for each ORM Data Type
	 */
	/**
	 * Delete all Doctor(s) from the ContentProvider, that match the selectionArgs
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return number of StoryData rows deleted
	 * @throws RemoteException
	 */
	public int deleteStatus(final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.delete(statusURI, selection, selectionArgs);
	}
	
	public int deleteDoctor(final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.delete(doctorURI, selection, selectionArgs);
	}
	
	public int deletePatient(final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.delete(patientURI, selection, selectionArgs);
	}
	
	public int deleteCheckin(final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.delete(checkinURI, selection, selectionArgs);
	}
	
	public int deletePatientMedication(final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.delete(patientMedicationURI, selection, selectionArgs);
	}
	
	public int deleteCheckinMedication(final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.delete(checkinMedicationURI, selection, selectionArgs);
	}	

	/**
	 * Get (MIME) Type for a URI
	 * 
	 * @param uri
	 * @return MIME TYPE as a String
	 * @throws RemoteException
	 */
	public String getType(Uri uri) throws RemoteException {
		return cr.getType(uri);
	}

	/*
	 * Query for each ORM Data Type
	 */

	/**
	 * Query for each Doctor, Similar to standard Content Provider query, just different return type
	 * 
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return an ArrayList of Doctor objects
	 * @throws RemoteException
	 */
	public ArrayList<Doctor> queryDoctor(final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor result = cr.query(doctorURI, projection, selection, selectionArgs, sortOrder);
		// make return object
		ArrayList<Doctor> rValue = new ArrayList<Doctor>();
		// convert cursor to reutrn object
		rValue.addAll(Doctor.getArrayListFromCursor(result));
		result.close();
		// return 'return object'
		return rValue;
	}

	public ArrayList<Patient> queryPatient(final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor result = cr.query(patientURI, projection, selection, selectionArgs, sortOrder);
		// make return object
		ArrayList<Patient> rValue = new ArrayList<Patient>();
		// convert cursor to reutrn object
		rValue.addAll(Patient.getArrayListFromCursor(result));
		result.close();
		// return 'return object'
		return rValue;
	}
	
	public ArrayList<Checkin> queryCheckin(final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor result = cr.query(checkinURI, projection, selection, selectionArgs, sortOrder);
		// make return object
		ArrayList<Checkin> rValue = new ArrayList<Checkin>();
		// convert cursor to reutrn object
		rValue.addAll(Checkin.getArrayListFromCursor(result));
		result.close();
		// return 'return object'
		return rValue;
	}
	
	public ArrayList<CheckinMedication> queryCheckinMedication(final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor result = cr.query(checkinMedicationURI, projection, selection, selectionArgs, sortOrder);
		// make return object
		ArrayList<CheckinMedication> rValue = new ArrayList<CheckinMedication>();
		// convert cursor to reutrn object
		rValue.addAll(CheckinMedication.getArrayListFromCursor(result));
		result.close();
		// return 'return object'
		return rValue;
	}
	
	public ArrayList<PatientMedication> queryPatientMedication(final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor result = cr.query(patientMedicationURI, projection, selection, selectionArgs, sortOrder);
		// make return object
		ArrayList<PatientMedication> rValue = new ArrayList<PatientMedication>();
		// convert cursor to reutrn object
		rValue.addAll(PatientMedication.getArrayListFromCursor(result));
		result.close();
		// return 'return object'
		return rValue;
	}	
	
	public Status queryStatus(final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor cursor = cr.query(statusURI, projection, selection, selectionArgs, sortOrder);
		// make return object
		Status status = null;
		// convert cursor to reutrn object
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				status = Status.getDataFromCursor(cursor);
			}
			cursor.close();
		}		
		// return 'return object'
		return status;
	}

	/*
	 * Update for each ORM Data Type
	 */

	/**
	 * do an Update for a StoryData, same input as standard Content Provider
	 * update
	 * 
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return number of rows changed
	 * @throws RemoteException
	 */
	public int updateDoctor(final Doctor values, final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.update(doctorURI, values.getContentValues(), selection, selectionArgs);
	}

	public int updatePatient(final Patient values, final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.update(patientURI, values.getContentValues(), selection, selectionArgs);
	}

	public int updateCheckin(final Checkin values, final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.update(checkinURI, values.getContentValues(), selection, selectionArgs);
	}
	
	public int updateCheckinMedication(final CheckinMedication values, final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.update(checkinMedicationURI, values.getContentValues(), selection, selectionArgs);
	}
	
	public int updatePatientMedication(final PatientMedication values, final String selection, final String[] selectionArgs) throws RemoteException {
		return cr.update(patientMedicationURI, values.getContentValues(), selection, selectionArgs);
	}

	/*
	 * Sample extensions of above for customized additional methods for classes
	 * that extend this one
	 */

	/**
	 * Get all the Doctor objects current stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the Doctor objects
	 * @throws RemoteException
	 */
	public ArrayList<Doctor> getAllDoctor() throws RemoteException {
		return queryDoctor(null, null, null, null);
	}

	/**
	 * Get all the Patient objects currently stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the Patient objects
	 * @throws RemoteException
	 */
	public ArrayList<Patient> getAllPatient() throws RemoteException {
		return queryPatient(null, null, null, null);
	}
	
	/**
	 * Get all the Checkin objects currently stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the Checkin objects
	 * @throws RemoteException
	 */
	public ArrayList<Checkin> getAllCheckin() throws RemoteException {
		return queryCheckin(null, null, null, null);
	}
	
	/**
	 * Get all the CheckinMedication objects currently stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the CheckinMedication objects
	 * @throws RemoteException
	 */
	public ArrayList<CheckinMedication> getAllCheckinMedication() throws RemoteException {
		return queryCheckinMedication(null, null, null, null);
	}
	
	/**
	 * Get all the patientMedication objects currently stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the patientMedication objects
	 * @throws RemoteException
	 */
	public ArrayList<PatientMedication> getAllPatientMedication() throws RemoteException {
		return queryPatientMedication(null, null, null, null);
	}	
	
	/**
	 * Get the patientMedication objects currently stored in the Content Provider where active is true
	 * 
	 * @return an ArrayList containing all the patientMedication objects with active value to true
	 * @throws RemoteException
	 */
	public ArrayList<PatientMedication> getActivePatientMedication() throws RemoteException {
		String[] selectionArgs = { String.valueOf(1) };
		return queryPatientMedication(null, SymptomSchema.PatientMedication.Cols.ACTIVE + "= ?", selectionArgs, null);
	}
	
	/**
	 * Get all check-ins not send to server yet.
	 * @return an ArrayList containing all the check-in objects not send to server
	 * @throws RemoteException
	 */
	public ArrayList<Checkin> getCheckinNotSendit() throws RemoteException {
		String[] selectionArgs = { String.valueOf(0) };
		return queryCheckin(null, SymptomSchema.Checkin.Cols.SEND + "= ?", selectionArgs, null);
	}
	
	/**
	 * Get all check-in medications objects that belongs to checkin
	 * @param checkinId check-in's id
	 * @return an ArrayList containing all the check-in medication objects 
	 * @throws RemoteException
	 */
	public ArrayList<CheckinMedication> getAllCheckinMedicationWithCheckinID(long checkinId) throws RemoteException {
		String[] selectionArgs = { String.valueOf(checkinId) };
		return queryCheckinMedication(null, SymptomSchema.CheckinMedication.Cols.ID_CHECKIN + "= ?", selectionArgs, null);
	}
	
	/**
	 * Get the status object currently stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the Patient objects
	 * @throws RemoteException
	 */
	public Status getStatus() throws RemoteException {
		return queryStatus(null, null, null, null);
	}		

	/**
	 * Get a Doctor from the database stored at the given rowID
	 * 
	 * @param rowID
	 * @return Doctor at the given rowID
	 * @throws RemoteException
	 */
	public Doctor getDoctorViaRowID(final long rowID) throws RemoteException {
		String[] selectionArgs = { String.valueOf(rowID) };
		ArrayList<Doctor> results = queryDoctor(null, SymptomSchema.Doctor.Cols.ID + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Get a Patient from the database stored at the given rowID
	 * 
	 * @param rowID
	 * @return Patient at the given rowID
	 * @throws RemoteException
	 */
	public Patient getPatientViaRowID(final long rowID) throws RemoteException {
		String[] selectionArgs = { String.valueOf(rowID) };
		ArrayList<Patient> results = queryPatient(null, SymptomSchema.Patient.Cols.ID + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}	
	
	/**
	 * Get a Checkin from the database stored at the given rowID
	 * 
	 * @param rowID
	 * @return Checkin at the given rowID
	 * @throws RemoteException
	 */
	public Checkin getCheckinViaRowID(final long rowID) throws RemoteException {
		String[] selectionArgs = { String.valueOf(rowID) };
		ArrayList<Checkin> results = queryCheckin(null, SymptomSchema.Checkin.Cols.ID + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}	

	/**
	 * Get a PatientMedication from the database stored at the given rowID
	 * 
	 * @param rowID
	 * @return PatientMedication at the given rowID
	 * @throws RemoteException
	 */
	public PatientMedication getPatientMedicationViaRowID(final long rowID) throws RemoteException {
		String[] selectionArgs = { String.valueOf(rowID) };
		ArrayList<PatientMedication> results = queryPatientMedication(null, SymptomSchema.PatientMedication.Cols.ID + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}	
	
	/**
	 * Get a CheckinMedication from the database stored at the given rowID
	 * 
	 * @param rowID
	 * @return CheckinMedication at the given rowID
	 * @throws RemoteException
	 */
	public CheckinMedication getCheckinMedicationViaRowID(final long rowID) throws RemoteException {
		String[] selectionArgs = { String.valueOf(rowID) };
		ArrayList<CheckinMedication> results = queryCheckinMedication(null, SymptomSchema.CheckinMedication.Cols.ID + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Get a CheckinMedication list from the database stored at the given checkinID
	 * 
	 * @param checkinID Checkin-in id
	 * @return an ArrayList of CheckinMedication objects at the given checkinID
	 * @throws RemoteException
	 */
	public ArrayList<CheckinMedication> getCheckinMedicationViaCheckinID(final long checkinID) throws RemoteException {
		String[] selectionArgs = { String.valueOf(checkinID) };
		ArrayList<CheckinMedication> results = queryCheckinMedication(null, SymptomSchema.CheckinMedication.Cols.ID_CHECKIN + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results;
		} else {
			return null;
		}
	}
	
	/**
	 * Get a PatientMedication list from the database stored at the given patientID
	 * 
	 * @param patientID Patient id
	 * @return an ArrayList of PatientMedication objects at the given patientID
	 * @throws RemoteException
	 */
	public ArrayList<PatientMedication> getPatientMedicationViaPatientID(final long patientID) throws RemoteException {
		String[] selectionArgs = { String.valueOf(patientID) };
		ArrayList<PatientMedication> results = queryPatientMedication(null, SymptomSchema.PatientMedication.Cols.ID_PATIENT + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results;
		} else {
			return null;
		}
	}

	/**
	 * Delete All rows, from Doctor table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllDoctorWithRowID(long rowID) throws RemoteException {
		String[] args = { String.valueOf(rowID) };
		return deleteDoctor(SymptomSchema.Doctor.Cols.ID + " = ? ", args);
	}

	/**
	 * Delete All rows, from Patient table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllPatientWithRowID(long rowID) throws RemoteException {
		String[] args = { String.valueOf(rowID) };
		return deletePatient(SymptomSchema.Patient.Cols.ID + " = ? ", args);
	}
	
	/**
	 * Delete All rows, from Checkin table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllCheckinWithRowID(long rowID) throws RemoteException {
		String[] args = { String.valueOf(rowID) };
		return deleteCheckin(SymptomSchema.Checkin.Cols.ID + " = ? ", args);
	}	
	
	/**
	 * Delete All rows, from CheckinMedication table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllCheckinMedicationWithRowID(long rowID) throws RemoteException {
		String[] args = { String.valueOf(rowID) };
		return deleteCheckinMedication(SymptomSchema.CheckinMedication.Cols.ID + " = ? ", args);
	}	
	
	/**
	 * Delete All rows, from PatientMedication table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllPatientMedicationWithRowID(long rowID) throws RemoteException {
		String[] args = { String.valueOf(rowID) };
		return deletePatientMedication(SymptomSchema.PatientMedication.Cols.ID + " = ? ", args);
	}	
	
	/**
	 * Delete All rows, from PatientMedication table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllPatientMedication() throws RemoteException {
		return deletePatientMedication(null, null);
	}

	/**
	 * Updates all Doctor stored with the provided StoryData's 'KEY_ID'
	 * (should only be 1 row of data in the content provider, but content
	 * provider implementation will update EVERY row that matches.)
	 * 
	 * @param data
	 * @return number of rows altered
	 * @throws RemoteException
	 */
	public int updateDoctorWithID(Doctor data) throws RemoteException {
		String selection = "_id = ?";
		String[] selectionArgs = { String.valueOf(data.getId()) };
		return updateDoctor(data, selection, selectionArgs);
	}
	
	/**
	 * Updates all Patient stored with the provided StoryData's 'KEY_ID'
	 * (should only be 1 row of data in the content provider, but content
	 * provider implementation will update EVERY row that matches.)
	 * 
	 * @param data
	 * @return number of rows altered
	 * @throws RemoteException
	 */
	public int updatePatientWithID(Patient data) throws RemoteException {
		String selection = "_id = ?";
		String[] selectionArgs = { String.valueOf(data.getId()) };
		return updatePatient(data, selection, selectionArgs);
	}
	
	/**
	 * Updates all Checkin stored with the provided StoryData's 'KEY_ID'
	 * (should only be 1 row of data in the content provider, but content
	 * provider implementation will update EVERY row that matches.)
	 * 
	 * @param data
	 * @return number of rows altered
	 * @throws RemoteException
	 */
	public int updateCheckinWithID(Checkin data) throws RemoteException {
		String selection = "_id = ?";
		String[] selectionArgs = { String.valueOf(data.getId()) };
		int n = updateCheckin(data, selection, selectionArgs);
		Log.d(TAG, "checkins updated:"+n);
		return n;
	}

	/**
	 * Updates all CheckinMedication stored with the provided StoryData's 'KEY_ID'
	 * (should only be 1 row of data in the content provider, but content
	 * provider implementation will update EVERY row that matches.)
	 * 
	 * @param data
	 * @return number of rows altered
	 * @throws RemoteException
	 */
	public int updateCheckinMedicationWithID(CheckinMedication data) throws RemoteException {
		String selection = "_id = ?";
		String[] selectionArgs = { String.valueOf(data.getId()) };
		return updateCheckinMedication(data, selection, selectionArgs);
	}
	
	/**
	 * Updates all PatientMedication stored with the provided StoryData's 'KEY_ID'
	 * (should only be 1 row of data in the content provider, but content
	 * provider implementation will update EVERY row that matches.)
	 * 
	 * @param data
	 * @return number of rows altered
	 * @throws RemoteException
	 */
	public int updatePatientMedicationWithID(PatientMedication data) throws RemoteException {
		String selection = "_id = ?";
		String[] selectionArgs = { String.valueOf(data.getId()) };
		return updatePatientMedication(data, selection, selectionArgs);
	}

}