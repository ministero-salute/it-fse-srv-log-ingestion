/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON Utility Class 
 * 
 *
 */
@Slf4j
public class JsonUtility {


    /**
     * Private constructor to avoid instantiation.
     *
     * @throws IllegalStateException
     */
    private JsonUtility() {

    }

    private static ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * Methods that converts an Object to a JSON string.
     *
     * @param obj  Object to convert.
     * @return Object  JSON String representation of the Object.
     */
    public static <T> String objectToJson(T obj) {
        String jsonString = "";

        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Errore durante la conversione da oggetto {} a string json: {}", obj.getClass(), e);
        }

        return jsonString;
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("Errore durante la conversione da stringa json a oggetto: " + e);
        }

        return obj;
    }


    public static <T> T clone (Object object, Class<T> outputClass) {
        return JsonUtility.jsonToObject(JsonUtility.objectToJson(object), outputClass);
    }
    
    public static <T> T validateJson(String jsonString, Class<T> clazz) {
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("Errore durante la conversione da stringa json a oggetto: " + e);
            throw new ValidationException("Fornire un json corretto");
        }

        return obj;
    }
}
