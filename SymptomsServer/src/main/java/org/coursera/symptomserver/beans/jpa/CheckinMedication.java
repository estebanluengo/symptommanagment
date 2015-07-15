package org.coursera.symptomserver.beans.jpa;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * JPA bean that contains a patients check-in's medications information. This object is mapped to CHECKIN_MEDICATION table
 */
@Entity
@Table(name = "CHECKIN_MEDICATION")
@XmlRootElement
public class CheckinMedication implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "TAKEIT")
    private boolean takeit;
    
    @Basic(optional = false)
    @Column(name = "TAKEIT_DATE")
    private String takeitDate;
    
    @Basic(optional = false)
    @Column(name = "TAKEIT_TIME")
    private String takeitTime;
    
    @JoinColumn(name = "ID_CHECKIN", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Checkin checkin;
    
    @JoinColumn(name = "ID_PATIENTMEDICATION", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PatientMedication patientMedication;
    
    public CheckinMedication() {
    }

    public CheckinMedication(Long id) {
        this.id = id;
    }

    public CheckinMedication(Long id, boolean takeit, String takeitDate, String takeitTime) {
        this.id = id;
        this.takeit = takeit;
        this.takeitDate = takeitDate;
        this.takeitTime = takeitTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getTakeit() {
        return takeit;
    }

    public void setTakeit(boolean takeit) {
        this.takeit = takeit;
    } 

    public String getTakeitDate() {
		return takeitDate;
	}

	public void setTakeitDate(String takeitDate) {
		this.takeitDate = takeitDate;
	}

	public String getTakeitTime() {
		return takeitTime;
	}

	public void setTakeitTime(String takeitTime) {
		this.takeitTime = takeitTime;
	}

	@JsonIgnore
    @XmlTransient
    //To avoid infinite loop parsing we avoid to send this object to clients
    public Checkin getCheckin() {
        return checkin;
    }

    public void setCheckin(Checkin checkin) {
        this.checkin = checkin;
    }
    
    public PatientMedication getPatientMedication() {
        return patientMedication;
    }

    public void setPatientMedication(PatientMedication patientMedication) {
        this.patientMedication = patientMedication;
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
        if (!(object instanceof CheckinMedication)) {
            return false;
        }
        CheckinMedication other = (CheckinMedication) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.coursera.symptom.beans.CheckinMedication[ id=" + id + " ]";
    }
    
}
