package it.finanze.sanita.fse2.ms.srv.logingestor.utility;

import com.google.gson.Gson;

import it.finanze.sanita.fse2.ms.srv.logingestor.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtility {
	

	/**
	 * Transformation from Object to Json.
	 * 
	 * @param obj	object to transform
	 * @return		json
	 */
	public static String toJSON(final Object obj) {
		return new Gson().toJson(obj);
	}
	

}
