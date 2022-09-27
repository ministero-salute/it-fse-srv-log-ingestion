/**
 * 
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.exceptions;

/**
 * @author vincenzoingenito
 * 
 * Connection refused error exception.
 *
 */
public class ConnectionRefusedException extends RuntimeException {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 1L;
	
	private final String url;

	public ConnectionRefusedException(final String inUrl,final String msg) {
		super(msg);
		url = inUrl;
	}

	public String getUrl() {
		return url;
	}

}
