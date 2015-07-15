package org.coursera.symptomserver.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * This class allow us to deserialize a String date to Date object
 * Every Object that need to use this conversion has to annotate set method with @JsonSerialize(using=DateTimeDeserializer.class) 
 * It uses ISO 8601 standard
 * 
 */
public class DateTimeDeserializer extends JsonDeserializer<Date> {

    private static final Logger logger = Logger.getLogger(DateTimeDeserializer.class);
    
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        String text = jp.getText();
        if (text == null){
            logger.warn("Text is null and can not be deserialized to date");
            return null;
        }
        if ("".equals(text)){
            logger.warn("Text is empty and can not be deserialized to date");
            return null;
        }
        DateTime date = new DateTime(text);        
        return date.toDate();
    }
    
}