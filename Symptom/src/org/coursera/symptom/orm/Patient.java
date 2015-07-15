package org.coursera.symptom.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.coursera.symptom.provider.SymptomSchema;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A bean that contains Patient information.
 */
public class Patient implements Serializable, Parcelable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String lastname;
    private String login;
    private String birthdate;
    private Doctor doctor;
    private List<PatientMedication> patientMedicationList;
    private List<Checkin> checkinList;

    public Patient() {
    }

    public Patient(Long id) {
        this.id = id;
    }

    public Patient(Long id, String name, String lastname, String login) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.login = login;
    }
    
    public Patient(Long id, String name, String lastname, String login, String birthDate, Long doctorId) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.login = login;
        this.birthdate = birthDate;
        this.doctor = new Doctor(doctorId);
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
    
    public String getFullName(){
    	return getName() + " " + getLastname();
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public List<PatientMedication> getPatientMedicationList() {
        return patientMedicationList;
    }

    public void setPatientMedicationList(List<PatientMedication> patientMedicationList) {
        this.patientMedicationList = patientMedicationList;
    }

    public List<Checkin> getCheckinList() {
        return checkinList;
    }

    public void setCheckinList(List<Checkin> checkinList) {
        this.checkinList = checkinList;
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
        if (!(object instanceof Patient)) {
            return false;
        }
        Patient other = (Patient) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.coursera.symptom.beans.Patient[ id=" + id + " ]";
    }

	public ContentValues getContentValues() {
		ContentValues rValue = new ContentValues();
		rValue.put(SymptomSchema.Patient.Cols.ID, getId());
		rValue.put(SymptomSchema.Patient.Cols.NAME, getName());
		rValue.put(SymptomSchema.Patient.Cols.LASTNAME, getLastname());
		rValue.put(SymptomSchema.Patient.Cols.LOGIN, getLogin());
		rValue.put(SymptomSchema.Patient.Cols.BIRTHDATE, getBirthdate());
		rValue.put(SymptomSchema.Patient.Cols.ID_DOCTOR, getDoctor().getId());
		return rValue;
	}

	public static ArrayList<Patient> getArrayListFromCursor(Cursor cursor) {
		ArrayList<Patient> rValue = new ArrayList<Patient>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					rValue.add(getDataFromCursor(cursor));
				} while (cursor.moveToNext() == true);
			}
		}
		return rValue;
	}

	public static Patient getDataFromCursor(Cursor cursor) {
		long rowID = cursor.getLong(cursor.getColumnIndex(SymptomSchema.Patient.Cols.ID));
		String name = cursor.getString(cursor.getColumnIndex(SymptomSchema.Patient.Cols.NAME));
		String lastName = cursor.getString(cursor.getColumnIndex(SymptomSchema.Patient.Cols.LASTNAME));
		String login = cursor.getString(cursor.getColumnIndex(SymptomSchema.Patient.Cols.LOGIN));
		String birthDate = cursor.getString(cursor.getColumnIndex(SymptomSchema.Patient.Cols.BIRTHDATE));
		long doctorId = cursor.getLong(cursor.getColumnIndex(SymptomSchema.Patient.Cols.ID_DOCTOR));
		return new Patient(rowID,name,lastName,login,birthDate,doctorId);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(lastname);
		dest.writeString(birthdate);		
	}	
	
	public static final Parcelable.Creator<Patient> CREATOR = new Parcelable.Creator<Patient>() {
		public Patient createFromParcel(Parcel in) {
			return new Patient(in);
		}

		public Patient[] newArray(int size) {
			return new Patient[size];
		}
	};

	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	private Patient(Parcel in) {
		this.id = in.readLong();
		this.name = in.readString();
		this.lastname = in.readString();
		this.birthdate = in.readString();		
	}
    
}