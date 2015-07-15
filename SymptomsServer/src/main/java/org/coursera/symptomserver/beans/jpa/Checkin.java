package org.coursera.symptomserver.beans.jpa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.coursera.symptomserver.utils.DateTimeDeserializer;
import org.coursera.symptomserver.utils.DateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * JPA bean that contains a patients' check-ins information. This object is mapped to CHECKIN table
 */
@Entity
@Table(name = "CHECKIN")
@XmlRootElement
public class Checkin implements Serializable {
	public static final String WELLCONTROLLED = "Well controlled";
	public static final String SEVERE = "Severe";
	public static final String MODERATE = "Moderate";
	public static final String ICANNOTEAT = "I can not eat";
	
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHECKIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkinDate;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "SEND")
    //this field is very useful and we use it to send check-ins to doctor that are not send it yet.
    private boolean send;   
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "HOWBAD")
    private String howbad;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "PAINSTOP")
    private String painstop;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "TAKEMEDICATION")
    private boolean takemedication;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "ALERTDOCTOR")
    //this field is useful and we use it to indicate doctor's mobile app that some patient needs medical attention.
    private boolean alertDoctor;  
    
    @Size(min = 1, max = 500)
    @Column(name = "PHOTO_PATH")
    private String photoPath; 
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "checkin", fetch = FetchType.LAZY)
    private List<CheckinMedication> checkinMedicationList;

    @JoinColumn(name = "ID_PATIENT", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Patient patient;
    
    public Checkin() {
    }

    public Checkin(Long id) {
        this.id = id;
    }

    public Checkin(Long id, Date checkinDate, boolean send, String howbad, String painstop, boolean takemedication) {
        this.id = id;
        this.checkinDate = checkinDate;
        this.send = send;
        this.howbad = howbad;
        this.painstop = painstop;
        this.takemedication = takemedication;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonSerialize(using=DateTimeSerializer.class)
    public Date getCheckinDate() {
        return checkinDate;
    }

    @JsonDeserialize(using=DateTimeDeserializer.class)
    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    public boolean getSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getHowbad() {
        return howbad;
    }

    public void setHowbad(String howbad) {
        this.howbad = howbad;
    }

    public String getPainstop() {
        return painstop;
    }

    public void setPainstop(String painstop) {
        this.painstop = painstop;
    }

    public boolean getTakemedication() {
        return takemedication;
    }

    public void setTakemedication(boolean takemedication) {
        this.takemedication = takemedication;
    }

    public boolean isAlertDoctor() {
		return alertDoctor;
	}

	public void setAlertDoctor(boolean alertDoctor) {
		this.alertDoctor = alertDoctor;
	}

    public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public List<CheckinMedication> getCheckinMedicationList() {
        return checkinMedicationList;
    }

    public void setCheckinMedicationList(List<CheckinMedication> checkinMedicationList) {
        this.checkinMedicationList = checkinMedicationList;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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
        if (!(object instanceof Checkin)) {
            return false;
        }
        Checkin other = (Checkin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.coursera.symptom.beans.Checkin[ id=" + id + " ]";
    }
    
}