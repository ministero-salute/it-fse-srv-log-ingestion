package it.finanze.sanita.fse2.ms.srv.logingestor.utility;

import com.google.gson.Gson;

public class StringUtility {
	
	private StringUtility() {
		
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
	
}
