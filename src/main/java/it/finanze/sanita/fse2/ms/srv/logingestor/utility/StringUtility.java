/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.utility;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtility {


	/**
	 * Private constructor to avoid instantiation.
	 */
	private StringUtility() {
		// Constructor intentionally empty.
	}

	/**
	 * Returns {@code true} if the String passed as parameter is null or empty.
	 * 
	 * @param str	String to validate.
	 * @return		{@code true} if the String passed as parameter is null or empty.
	 */
	public static boolean isNullOrEmpty(final String str) {
		return str == null || str.isEmpty();
	}
    
	public static String generateUUID() {
	    return UUID.randomUUID().toString();
	}

	public static String generateTransactionUID() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
	}

	/**
	 * Transformation from Json to Object.
	 * 
	 * @param <T>	Generic type of return
	 * @param json	json
	 * @param cls	Object class to return
	 * @return		object
	 */
	public static <T> T fromJSON(final String json, final Class<T> cls) {
		return new Gson().fromJson(json, cls);
	}

	/**
	 * Transformation from Object to Json.
	 * 
	 * @param obj	object to transform
	 * @return		json
	 */
	public static String toJSON(final Object obj) {
		return new Gson().toJson(obj);
	}
	
	/**
	 * Transformation from Object to Json.
	 * 
	 * @param obj	object to transform
	 * @return		json
	 */
	public static String toJSONJackson(final Object obj) {
		String out = "";
		try {
			final ObjectMapper objectMapper = new ObjectMapper(); 
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
			objectMapper.setTimeZone(TimeZone.getDefault());
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			out = objectMapper.writeValueAsString(obj);
		} catch(final Exception ex) {
			log.error("Error while running to json jackson");
			throw new BusinessException(ex);
		}
		return out; 
	}
  
}
