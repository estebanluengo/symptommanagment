package org.coursera.symptomserver.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains validation error information that it is send to clients.
 */
public class ValidationError {

	//Fields list that don't match validation constraints
    private final List<FieldError> fieldErrors = new ArrayList<>();
  //URL that produces the error
    private String URI;

    public ValidationError() {

    }
    
    public void addFieldError(String path, String message) {
        FieldError error = new FieldError(path, message);
        getFieldErrors().add(error);
    }

    /**
     * @return the fieldErrors
     */
    @JsonProperty("fieldErrors")
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * @return the URI
     */
    public String getURI() {
        return URI;
    }

    /**
     * @param URI the URI to set
     */
    public void setURI(String URI) {
        this.URI = URI;
    }
   
}
