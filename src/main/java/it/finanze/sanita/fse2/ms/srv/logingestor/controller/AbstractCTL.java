package it.finanze.sanita.fse2.ms.srv.logingestor.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogTraceInfoDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCTL implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 145052693293739719L;
	
	@Autowired
	private Tracer tracer;

	protected LogTraceInfoDTO getLogTraceInfo() {
		LogTraceInfoDTO out = new LogTraceInfoDTO(null, null);
		if (tracer.currentSpan() != null) {
			out = new LogTraceInfoDTO(
					tracer.currentSpan().context().spanIdString(), 
					tracer.currentSpan().context().traceIdString());
		}
		return out;
	}

}