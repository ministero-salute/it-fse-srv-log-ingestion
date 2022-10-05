package it.finanze.sanita.fse2.ms.srv.logingestor.dto.response;

import java.time.LocalDate;

import it.finanze.sanita.fse2.ms.srv.logingestor.dto.AbstractDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDTO extends AbstractDTO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 189829250370457318L;

	String log_type;
	
	String message;
	
	String operation;
	
	String op_result;
	
	LocalDate op_timestamp_start;
	
	LocalDate op_timestamp_end;
	
	String op_issuer;
	
	String op_document_type;

}
