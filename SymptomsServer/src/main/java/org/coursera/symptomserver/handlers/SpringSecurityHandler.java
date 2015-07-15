package org.coursera.symptomserver.handlers;

import java.util.Collection;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

/**
 * Security utility class for dealing with authenticated users
 */
@Component
public class SpringSecurityHandler implements AuditorAware<User> {

    public static String DOCTOR_ROLE = "DOCTOR";
    public static String PATIENT_ROLE = "PATIENT";
    public static String UNKNOWN_ROLE = "UNKNOWN";
    
    /**
     * Ger logged user
     *
     * @return this method returns and Spring User object
     * @see org.springframework.security.core.userdetails.User
     */
    @Override
    public User getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        User user = (User) authentication.getPrincipal();
        return user;
    }
    
    /**
     * Returns user name of logged user
     * @return 
     */
    public String getUserName(){
        String userName = getCurrentAuditor().getUsername();
        return userName;
    }

    /**
     * Returns user role of logged user. Options are:
     * DOCTOR_ROLE for doctors
     * PATIENT_ROLE for patients
     * UNKNOWN for Unknown users
     * 
     * @return a String that represents user role
     */
    public String getUserRole(){
        Collection<GrantedAuthority> authorities = getCurrentAuditor().getAuthorities();
        for (GrantedAuthority authority:authorities){
            switch(authority.getAuthority()){
                case "DOCTOR":
                    return DOCTOR_ROLE;
                case "PATIENT":
                    return PATIENT_ROLE;                    
            }            
        }  
        return UNKNOWN_ROLE;
    }
}
