package org.coursera.symptom.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.coursera.symptom.provider.SymptomSchema;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * A bean that contains Doctors information.
 */
public class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String lastname;
    private String login;
    private List<Patient> patientList;

    public Doctor() {
    }

    public Doctor(Long id) {
        this.id = id;
    }

    public Doctor(Long id, String name, String lastname, String login) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.login = login;
    }

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doctor)) {
            return false;
        }
        Doctor other = (Doctor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.coursera.symptom.beans.Doctor[ id=" + id + " ]";
    }

    /**
     * Builds a ContentValues object using the properties of this object
     * @return a ContentValues object
     */
	public ContentValues getContentValues() {
		ContentValues rValue = new ContentValues();
		rValue.put(SymptomSchema.Doctor.Cols.ID, getId());
		rValue.put(SymptomSchema.Doctor.Cols.NAME, getName());
		rValue.put(SymptomSchema.Doctor.Cols.LASTNAME, getLastname());
		rValue.put(SymptomSchema.Doctor.Cols.LOGIN, getLogin());
		return rValue;
	}

	/**
     * Gets all information from cursor and returns an ArrayList of Doctor objects
     * 
     * @param cursor a Cursor object that is mapped to the local database
     * @return a ArrayList<Doctor>
     */
	public static ArrayList<Doctor> getArrayListFromCursor(Cursor cursor) {
		ArrayList<Doctor> rValue = new ArrayList<Doctor>();
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
	 * This method creates a new Doctor object getting the data from cursor object
	 * 
	 * @param cursor a Cursor object that is mapped to the local database
	 * @return a Doctor object with data read from cursor
	 */
	public static Doctor getDataFromCursor(Cursor cursor) {
		long rowID = cursor.getLong(cursor.getColumnIndex(SymptomSchema.Doctor.Cols.ID));
		String name = cursor.getString(cursor.getColumnIndex(SymptomSchema.Doctor.Cols.NAME));
		String lastName = cursor.getString(cursor.getColumnIndex(SymptomSchema.Doctor.Cols.LASTNAME));
		String login = cursor.getString(cursor.getColumnIndex(SymptomSchema.Doctor.Cols.LOGIN));
		return new Doctor(rowID,name,lastName,login);
	}    
    
}