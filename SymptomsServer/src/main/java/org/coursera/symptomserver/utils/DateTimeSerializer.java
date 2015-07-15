package org.coursera.symptomserver.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * This class allow to RestController to serialize a Date object to String date.
 * Every Object that need to use this conversion has to annotate get method with @JsonSerialize(using=DateTimeSerializer.class) 
 * 
 * It uses ISO 8601 standard
 * 
 */
public class DateTimeSerializer  extends JsonSerializer<Date>{

    private static final Logger logger = Logger.getLogger(DateTimeSerializer.class);
    
    @Override
    public void serialize(Date t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        if (t != null) {
            DateTime date = new DateTime(t);
            jg.writeString(date.toString());
        }else{
            logger.warn("Date is null and can not be serialized to String");
        }
    }
    
}

