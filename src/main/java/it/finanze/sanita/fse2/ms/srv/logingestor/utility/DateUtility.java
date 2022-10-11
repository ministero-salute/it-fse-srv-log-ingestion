package it.finanze.sanita.fse2.ms.srv.logingestor.utility;

import java.util.Calendar;
import java.util.Date;

public class DateUtility {

	private DateUtility() {

	}
 
	/**
	 * Set hours to Date
	 */
	public static Date setHoursToDate(Date date, int hours, int minutes, int seconds) {
		Date result = null;
		
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, seconds);
	    
		result = calendar.getTime();
		
		return result;
	}
}
