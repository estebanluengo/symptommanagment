package org.coursera.symptomserver.repository;

import java.util.Date;
import java.util.List;
import org.coursera.symptomserver.beans.jpa.Checkin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository class for database Checkin table
 */
@Repository
public interface CheckinRepository extends JpaRepository<Checkin, Long>{
    
	/**
	 * Returns a Checkin object that corresponds to checkinId and it is associated to patient userName.
	 * 
	 * @param userName a String that represents userName of logged patient 
	 * @param checkinId a Long that represetns Checkin Id
	 * @return a Checkin object without any CheckinInformation list but with a Patient and Doctor object associated.
	 * @see org.coursera.symptomserver.beans.Checkin
	 */
	@Query("select ch from Checkin ch inner join ch.patient pa where pa.login = :userName and ch.id = :checkinId")
	public Checkin findOneByUserName(@Param("userName") String userName, @Param("checkinId") Long checkinId);
	
	/**
	 * Returns a Checkin object that corresponds to checkinId and its patient is associated to doctor doctorName.
	 * 
	 * @param doctorName a String that represents doctor name 
	 * @param checkinId a Long that represetns Checkin Id
	 * @return a Checkin object without any CheckinInformation list but with a Patient and Doctor object associated.
	 * @see org.coursera.symptomserver.beans.Checkin
	 */
	@Query("select ch from Checkin ch inner join ch.patient pa inner join pa.doctor do where do.login = :doctorName and ch.id = :checkinId")
	public Checkin findOneByDoctorName(@Param("doctorName") String doctorName, @Param("checkinId") Long checkinId);
	
	/**
	 * Returns the Doctor patient's check-ins for a particular Patient with patientId. 
     * The method returns the last MAX_CHECKIN_RESULTS check-ins. 
     * 
	 * @param doctorUserName a String that represents userName of logged doctor
	 * @param patientId a Long that represents patient Id
	 * @param pageable a Pageable object with MAX_CHECKIN_RESULTS set
	 * @return a List<Checkin>. Every checkin object contains a CheckinMedication list, a Patient and Doctor object.
	 * @see org.coursera.symptomserver.beans.Checkin
	 * @see org.coursera.symptomserver.beans.CheckinMedication
	 */
    @Query("select distinct ch from Checkin ch inner join ch.patient pa inner join pa.doctor do "
            + "left outer join fetch ch.checkinMedicationList cml left outer join fetch cml.patientMedication "
            + " where pa.id = :patientId and do.login = :doctorUserName "
            + "order by ch.checkinDate desc")
    public List<Checkin> findAllCheckins(@Param("doctorUserName") String doctorUserName, 
            @Param("patientId") Long patientId, Pageable pageable);
    
    /**
	 * Returns the Doctor patient's check-ins for a particular Patient with patientName. 
     * The method returns the last MAX_CHECKIN_RESULTS check-ins. 
     * 
	 * @param doctorUserName a String that represents userName of logged doctor
	 * @param patientName a String that represents patientName
	 * @param pageable a Pageable object with MAX_CHECKIN_RESULTS set
	 * @return a List<Checkin>. Every checkin object contains a CheckinMedication list, a Patient and Doctor object.
	 * @see org.coursera.symptomserver.beans.Checkin
	 * @see org.coursera.symptomserver.beans.CheckinMedication
	 */
    @Query("select distinct ch from Checkin ch inner join ch.patient pa inner join pa.doctor do "
            + "left outer join fetch ch.checkinMedicationList cml left outer join fetch cml.patientMedication "
            + " where pa.name = :patientName and do.login = :doctorUserName "
            + "order by ch.checkinDate desc")
    public List<Checkin> findAllCheckins(@Param("doctorUserName") String doctorUserName, 
            @Param("patientName") String patientName, Pageable pageable);
    
    /**
	 * Returns the Doctor patients' check-ins from a specific date dateFrom.  
     * 
	 * @param doctorUserName a String that represents userName of logged doctor
	 * @param fromDate a Date with a specific date
	 * @return a List<Checkin>. Every checkin object contains a CheckinMedication list, a Patient and Doctor object.
	 * @see org.coursera.symptomserver.beans.Checkin
	 * @see org.coursera.symptomserver.beans.CheckinMedication
	 */
    @Query("select distinct ch from Checkin ch inner join ch.patient pa inner join pa.doctor do "
            + "left outer join fetch ch.checkinMedicationList cml left outer join fetch cml.patientMedication "
            + " where do.login = :doctorUserName and ch.checkinDate >= :fromDate "
            + "order by pa.id, ch.checkinDate desc")
    public List<Checkin> findAllCheckins(@Param("doctorUserName") String doctorUserName, 
            @Param("fromDate") Date fromDate);
    
    /**
	 * Returns the Doctor patient's check-ins for a particular Patient with patientName and specific date. 
     * 
	 * @param doctorUserName a String that represents userName of logged doctor
	 * @param fromDate a Date with a specific date
	 * @param patientName a String that represents patientName
	 * @param pageable a Pageable object with MAX_CHECKIN_RESULTS set
	 * @return a List<Checkin>. Every checkin object contains a CheckinMedication list, a Patient and Doctor object.
	 * @see org.coursera.symptomserver.beans.Checkin
	 * @see org.coursera.symptomserver.beans.CheckinMedication
	 */
    @Query("select distinct ch from Checkin ch inner join ch.patient pa inner join pa.doctor do "
            + "left outer join fetch ch.checkinMedicationList cml left outer join fetch cml.patientMedication "
            + " where pa.name = :patientName and do.login = :doctorUserName and ch.checkinDate >= :fromDate "
            + "order by ch.checkinDate desc")
    public List<Checkin> findAllCheckins(@Param("doctorUserName") String doctorUserName, 
            @Param("patientName") String patientName, @Param("fromDate") Date fromDate);
    
    /**
	 * Returns the Doctor patients' check-ins not send it yet or already send it to doctor
     * 
	 * @param doctorUserName a String that represents userName of logged doctor
	 * @param send a boolean with true value to indicate that we want check-in already send it or false
	 * value to indicate that we want check-ins not send it yet
	 * @return a List<Checkin>. Every checkin object contains a CheckinMedication list
	 * @see org.coursera.symptomserver.beans.Checkin
	 * @see org.coursera.symptomserver.beans.CheckinMedication
	 */
    @Query("select distinct ch from Checkin ch inner join fetch ch.patient pa inner join pa.doctor do "
            + "left outer join fetch ch.checkinMedicationList cml left outer join fetch cml.patientMedication "
            + " where do.login = :doctorUserName and ch.send = :send "
            + "order by ch.checkinDate desc")
    public List<Checkin> findAllCheckinsBySend(@Param("doctorUserName") String doctorUserName, @Param("send") boolean send);
    
    /**
	 * Returns the patient's check-ins for a particular Patient with patientName and created after the time fromTime  
     * 
	 * @param patientId a Long that corresponds to patient Id
	 * @param fromTime a Date object that corresponds to specific date and time to search Checki-ns since this value. 
	 * @return a List<Checkin>. Every checkin object contains a Patient object associated
	 * @see org.coursera.symptomserver.beans.Checkin
	 * @see org.coursera.symptomserver.beans.CheckinMedication
	 */
    @Query("select distinct ch from Checkin ch inner join ch.patient pa  "
            + " where pa.id = :patientId and ch.checkinDate >= :fromTime "
            + "order by ch.checkinDate asc")
    public List<Checkin> findAllCheckinsFromTime(@Param("patientId") Long patientId, @Param("fromTime") Date fromTime);
}