package it.finanze.sanita.fse2.ms.srv.logingestor.dto.response;

import javax.validation.constraints.Size;

import it.finanze.sanita.fse2.ms.srv.logingestor.dto.AbstractDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO extends AbstractDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5688402530648153750L;

	/**
	 * Trace id log.
	 */
	@Size(min = 0, max = 100)
	private String traceID;

	/**
	 * Span id log.
	 */
	@Size(min = 0, max = 100)
	private String spanID;

	/**
	 * Instantiates a new response DTO.
	 */
	public ResponseDTO() {
	}

	/**
	 * Instantiates a new response DTO.
	 *
	 * @param traceInfo the trace info
	 */
	public ResponseDTO(final LogTraceInfoDTO traceInfo) {
		traceID = traceInfo.getTraceID();
		spanID = traceInfo.getSpanID();
	}

}
