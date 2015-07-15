package org.coursera.symptomserver.handlers;

import org.coursera.symptomserver.errors.ErrorInfo;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coursera.symptomserver.errors.ValidationError;
import org.coursera.symptomserver.exceptions.AccessException;
import org.coursera.symptomserver.exceptions.GeneralException;
import org.coursera.symptomserver.exceptions.NotFoundException;
import org.coursera.symptomserver.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global Exception handler controller. All Exceptions that occurs in the server are managed here.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalControllerExceptionHandler.class.getName());
    
    @Autowired
    private MessageSource messages;

    /**
     * This method receives errors from JPA layer that corresponds to ConstraintViolationException class
     * 
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param cve a ConstraintViolationException object. Contains constraint object information
     * @return a ValidationError object to client with the error information and HttpStatus.INTERNAL_SERVER_ERROR status code
     * @see org.coursera.symptomserver.errors.ValidationError
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ValidationError handleConstraintValidationException(HttpServletRequest req, ConstraintViolationException cve) {
        try {
            ValidationError ve = ValidationUtils.processConstraintsViolations(cve, messages, LocaleContextHolder.getLocale());
            ve.setURI(req.getRequestURI());
            return ve;
        } catch (Exception e) {
            logger.log(Level.ALL, "handleConstraintValidationException", e);
            return null;
        } finally {
            logger.log(Level.ALL, "handleConstraintValidationException", cve);
        }
    }

    /**
     * This method receives errors that corresponds to MethodArgumentNotValidException class. This exception class
     * Occurs when methods' arguments are not valid
     *
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param marve a MethodArgumentNotValidException object with error information
     * @return a ValidationError object to client with the error information and HttpStatus.BAD_REQUEST status code
     * @see org.coursera.symptomserver.errors.ValidationError
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ValidationError processValidationError(HttpServletRequest req, MethodArgumentNotValidException marve) {
        try {
            BindingResult result = marve.getBindingResult();
            List<FieldError> fieldErrors = result.getFieldErrors();
            ValidationError ve = ValidationUtils.processFieldErrors(fieldErrors, messages, LocaleContextHolder.getLocale());
            ve.setURI(req.getRequestURI());
            return ve;
        } catch (Exception e) {
            logger.log(Level.ALL, "processValidationError", e);
            return null;
        } finally {
            logger.log(Level.ALL, "processValidationError", marve);
        }
    }

    /**
     * This method receives errors that corresponds to HttpMediaTypeNotAcceptableException class.
     *
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param ex a HttpMediaTypeNotAcceptableException object. Corresponds to original Exception
     * @return a ErrorInfo object to client with the error information and HttpStatus.NOT_ACCEPTABLE status code
     * @see org.coursera.symptomserver.errors.ErrorInfo
     */
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public ErrorInfo handleMediaExcepton(HttpServletRequest req, HttpMediaTypeNotAcceptableException ex) {
    	ex.printStackTrace();
        logger.log(Level.ALL, "handleMediaExcepton", ex);
        String mensaje = messages.getMessage("CONTENTTYPE_ERROR", null, LocaleContextHolder.getLocale());
        return new ErrorInfo(req.getRequestURI(), ex, mensaje);
    }

    /**
     * This method receives errors that corresponds to HttpMediaTypeNotSupportedException class.
     *
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param ex a HttpMediaTypeNotSupportedException object. Corresponds to original Exception
     * @return a ErrorInfo object to client with the error information and HttpStatus.BAD_REQUEST status code
     * @see org.coursera.symptomserver.errors.ErrorInfo
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ErrorInfo handleMediaNotSuported(HttpServletRequest req, HttpMediaTypeNotSupportedException ex) {
    	ex.printStackTrace();
        logger.log(Level.ALL, "handleMediaNotSuported", ex);
        String mensaje = messages.getMessage("CONTENTTYPE_ERROR", null, LocaleContextHolder.getLocale());
        return new ErrorInfo(req.getRequestURI(), ex, mensaje);
    }  
    
    /**
     * This method receives errors that corresponds to AccessException class.
     *
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param ex a AccessException object. Corresponds to original Exception
     * @return a ErrorInfo object to client with the error information and HttpStatus.UNAUTHORIZED status code
     * @see org.coursera.symptomserver.errors.ErrorInfo
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessException.class)
    @ResponseBody
    public ErrorInfo handleNotAuthorized(HttpServletRequest req, AccessException ex) {
    	ex.printStackTrace();
        logger.log(Level.DEBUG, "handleNotAuthorized", ex);
        String mensaje = messages.getMessage(ex.getErrorCode(), null, LocaleContextHolder.getLocale());
        return new ErrorInfo(req.getRequestURI(), ex, mensaje);
    }
    
    /**
     * This method receives errors that corresponds to NotFoundException class.
     *
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param ex a NotFoundException object. Corresponds to original Exception
     * @return a ErrorInfo object to client with the error information and HttpStatus.UNAUTHORIZED status code
     * @see org.coursera.symptomserver.errors.ErrorInfo
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorInfo handleNotFount(HttpServletRequest req, NotFoundException ex) {
    	ex.printStackTrace();
        logger.log(Level.DEBUG, "handleNotFount", ex);
        String mensaje = messages.getMessage(ex.getErrorCode(), null, LocaleContextHolder.getLocale());
        return new ErrorInfo(req.getRequestURI(), ex, mensaje);
    }
    
    /**
     * This method receives errors that corresponds to GeneralException class.
     *
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param ex a GeneralException object. Corresponds to original Exception
     * @return a ErrorInfo object to client with the error information and HttpStatus.BAD_REQUEST status code
     * @see org.coursera.symptomserver.errors.ErrorInfo
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GeneralException.class)
    @ResponseBody
    public ErrorInfo handleGeneralException(HttpServletRequest req, GeneralException ex) {
    	ex.printStackTrace();
        logger.log(Level.DEBUG, "handleGeneralException", ex);
        String mensaje = messages.getMessage(ex.getErrorCode(), null, LocaleContextHolder.getLocale());
        return new ErrorInfo(req.getRequestURI(), ex, mensaje);
    }    

    /**
     * This method receives errors that corresponds to Exception class.
     *
     * @param req a HttpServletRequest object. Useful to Know requestUri that calls Rest method
     * @param ex a Exception object. Corresponds to original Exception
     * @return a ErrorInfo object to client with the error information and HttpStatus.INTERNAL_SERVER_ERROR status code
     * @see org.coursera.symptomserver.errors.ErrorInfo
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo handleInternalError(HttpServletRequest req, Exception ex) {
    	ex.printStackTrace();
        logger.log(Level.DEBUG, "handleInternalError", ex);
        String mensaje = messages.getMessage("GENERAL_ERROR", null, LocaleContextHolder.getLocale());
        return new ErrorInfo(req.getRequestURI(), ex, mensaje);
    }
    
}
