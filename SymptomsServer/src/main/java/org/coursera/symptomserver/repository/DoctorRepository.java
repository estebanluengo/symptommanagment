package org.coursera.symptomserver.repository;

import org.coursera.symptomserver.beans.jpa.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *  Repository class to access database Doctor Table
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{

	/**
	 * Returns a Doctor with login that corresponds to login parameter
	 * @param login a String with login data
	 * @return a Doctor object
	 * @see org.coursera.symptomserver.beans.Doctor  
	 */
    public Doctor findByLogin(String login);
    
}
