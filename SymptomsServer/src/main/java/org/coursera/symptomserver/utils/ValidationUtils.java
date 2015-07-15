package org.coursera.symptomserver.utils;

import java.util.List;
import java.util.Locale;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Logger;
import org.coursera.symptomserver.errors.ValidationError;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;

/**
 * Utility class to validate fields constraints
 */
public class ValidationUtils {
    
    private static final Logger logger = Logger.getLogger(ValidationUtils.class);
    
    /**
     * This method converts a List of FiledError into a ValidationError object
     * @param fieldErrors a List<FiledError> that represents all fields with validation constraints errors
     * @param messages a MessageSource object that represents all messages properties files
     * @param locale a Locale object that represents Locale user 
     * @return a ValidationError object
     * @see org.coursera.symptomserver.errors.ValidationError
     */
    public static ValidationError processFieldErrors(List<FieldError> fieldErrors, MessageSource messages, Locale locale) {
        logger.info("Calling processFieldErrors");
        ValidationError ve = new ValidationError();
        
        for (FieldError fieldError: fieldErrors) {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError, messages, locale);
            ve.addFieldError(fieldError.getField(), localizedErrorMessage);
        }
        return ve;
    }
    
    /**
     * This method converts a FiledError into a textual human error
     * @param fieldError a FiledError object that represents the field with validation constraints errors
     * @param messages a MessageSource object that represents all messages properties files
     * @param locale a Locale object that represents Locale user 
     * @return a String with textual error
     */
    private static String resolveLocalizedErrorMessage(FieldError fieldError, MessageSource messages, Locale locale) {
        logger.info("Calling resolveLocalizedErrorMessage");
        String localizedErrorMessage = messages.getMessage(fieldError, locale);

        //Si el mensaje no se encuentra se busca el mensaje asociado al código del campo.
        if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
            String[] fieldErrorCodes = fieldError.getCodes();
            localizedErrorMessage = fieldErrorCodes[0];
        }
        return localizedErrorMessage;
    }

    /**
     * This method converts a ConstraintViolationException object into a ValidationError object
     * @param cve a ConstraintViolationException object that represents all fields with validation constraints errors
     * @param messages a MessageSource object that represents all messages properties files
     * @param userLocale a Locale object that represents Locale user 
     * @return a ValidationError object
     * @see org.coursera.symptomserver.errors.ValidationError
     */
    public static ValidationError processConstraintsViolations(ConstraintViolationException cve, MessageSource messages, Locale userLocale) {
        logger.info("Calling processConstraintsViolations");
        ValidationError ve = new ValidationError();
        for (ConstraintViolation<?> cv: cve.getConstraintViolations()){             
           String messageTemplate = cv.getMessageTemplate();
           String localizedErrorMessage = messages.getMessage(messageTemplate, new Object[] { cv.getLeafBean().getClass().getSimpleName(), cv.getPropertyPath().toString(),
						cv.getInvalidValue() }, cv.getMessage(), userLocale);
//           ConstraintDescriptor cd = cv.getConstraintDescriptor();
           logger.debug("Validation error for field:"+cv.getPropertyPath().toString()+ ". Message error:"+localizedErrorMessage);
           ve.addFieldError(cv.getPropertyPath().toString(), localizedErrorMessage);                  
        }    
        return ve;
    }
}