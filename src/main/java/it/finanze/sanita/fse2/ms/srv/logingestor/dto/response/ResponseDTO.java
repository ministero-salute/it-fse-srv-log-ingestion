package it.finanze.sanita.fse2.ms.srv.logingestor.dto.response;

import it.finanze.sanita.fse2.ms.srv.logingestor.dto.AbstractDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO extends AbstractDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1630931782215900987L;

	String id;
	
	String region;
	
	DocumentDTO document;
	
}
