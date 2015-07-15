package org.coursera.symptomserver.beans;

import org.coursera.symptomserver.beans.jpa.Doctor;
import org.coursera.symptomserver.beans.jpa.Patient;

/**
 * Bean container that represents user information.
 */
public class Status {

	//If the user is a patient, this object contains their information
    private Patient patient;
    //If the user is a doctor, this object contains their information
    private Doctor doctor;
    //User role. It can be "PATIENT" or "DOCTOR"
    private String role;

    public Status() {
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
    
}