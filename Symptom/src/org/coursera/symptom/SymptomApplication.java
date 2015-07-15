package org.coursera.symptom;

import java.util.ArrayList;

import org.coursera.symptom.orm.Patient;

import android.app.Application;

/**
 * Class that extends Android Application in order to save globally data.
 * In this case we save patient information selected by doctor in the first Activity PatientList
 *
 */
public class SymptomApplication extends Application{

	//Patient object that has been selected in PatientList activity
	private Patient patient;
	//Contains Patient list with monitor needs
	private ArrayList<Patient> monitorList;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public ArrayList<Patient> getMonitorList() {
		return monitorList;
	}

	public void setMonitorList(ArrayList<Patient> monitorList) {
		this.monitorList = monitorList;
	}
	
	
}
