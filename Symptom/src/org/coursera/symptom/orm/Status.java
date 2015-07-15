package org.coursera.symptom.orm;

import java.util.Date;
import java.util.ArrayList;

import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.orm.Doctor;
import org.coursera.symptom.orm.Patient;
import org.coursera.symptom.provider.SymptomSchema;
import org.coursera.symptom.utils.Utils;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * A bean that represents user information.
 */
public class Status {
	
	//What is the role of the user
    private String role;
    //Id in the server
    private Long id;
    private Date dateInsert;
    //If the user is a patient here are their personal information
    private Patient patient;
    //If the user is a doctor here are their personal information and if the
    //user is a patient here are their doctor's personal information
    private Doctor doctor;

    public Status() {
    }
    
    public Status(String role) {
		this.role = role;
	}

	public Status(long id, String role, Date dateInsert) {
		this.id = id;
		this.role = role;
		this.dateInsert = dateInsert;
	}

	public Long getId() {
		if (id == null){ //if id is null, try to recover from patient or doctor objects
			setId();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Fix de id attribute depending if is a doctor or patient
	 */
	public void setId() {
		if (isAPatient()){
			setId(patient.getId());
		}else{
			if (isADoctor()){
				setId(doctor.getId());
			}
		}		
	}   

	public Date getDateInsert() {
		return dateInsert;
	}

	public void setDateInsert(Date dateInsert) {
		this.dateInsert = dateInsert;
	}

	public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public boolean isAPatient() {
    	return ((role != null) && (role.equals(SymptomValues.ROLE_PATIENT)));
    }
    
    public boolean isADoctor() {
    	return (role != null) && (role.equals(SymptomValues.ROLE_DOCTOR));
    }
    
    /**
     * Builds a ContentValues object using the properties of this object
     * @return a ContentValues object
     */
    public ContentValues getContentValues() {
		ContentValues rValue = new ContentValues();
		rValue.put(SymptomSchema.Status.Cols.ID, getId());
		rValue.put(SymptomSchema.Status.Cols.ROLE_NAME, getRole());
		rValue.put(SymptomSchema.Status.Cols.DATE_INSERT, Utils.dateAndTimeToString(getDateInsert()));
		return rValue;
	}

    /**
     * Gets all information from cursor and returns an ArrayList of Status objects
     * 
     * @param cursor a Cursor object that is mapped to the local database
     * @return a ArrayList<Status>
     */
	public static ArrayList<Status> getArrayListFromCursor(Cursor cursor) {
		ArrayList<Status> rValue = new ArrayList<Status>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					rValue.add(getDataFromCursor(cursor));
				} while (cursor.moveToNext() == true);
			}
		}
		return rValue;
	}
	
	/**
	 * This method creates a new Status object getting the data from cursor object
	 * 
	 * @param cursor a Cursor object that is mapped to the local database
	 * @return a Status object with data read from cursor
	 */
	public static Status getDataFromCursor(Cursor cursor) {
		long rowID = cursor.getLong(cursor.getColumnIndex(SymptomSchema.Status.Cols.ID));
		String role = cursor.getString(cursor.getColumnIndex(SymptomSchema.Status.Cols.ROLE_NAME));
		Date dateInsert = Utils.stringToDateAndTime(cursor.getString(cursor.getColumnIndex(SymptomSchema.Status.Cols.DATE_INSERT)));
		return new Status(rowID,role,dateInsert);
	}

	 
    
}