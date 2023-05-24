/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.controller.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;

/**
 *	Exceptions Handler.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {


	/**
	 * Tracker log.
	 */
	@Autowired
	private Tracer tracer;

	@ExceptionHandler(value = {ValidationException.class})
	protected ResponseEntity<ErrorResponseDTO> handleGenericValidationException(final ValidationException ex, final WebRequest request) {
		log.error("" , ex);  
		Integer status = 400;
		 
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

		ErrorResponseDTO out = new ErrorResponseDTO(getLogTraceInfo(), "/msg/validation", "Validation error", ex.getMessage(), status, "/msg/validation");
		return new ResponseEntity<>(out, headers, status);
	}

	@ExceptionHandler(value = {Exception.class})
	protected ResponseEntity<ErrorResponseDTO> handleGenericValidationException(final Exception ex, final WebRequest request) {
		log.error("" , ex);  
		Integer status = 500;
		 
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

		ErrorResponseDTO out = new ErrorResponseDTO(getLogTraceInfo(), "/msg/validation", "Validation error", ex.getMessage(), status, "/msg/validation");
		return new ResponseEntity<>(out, headers, status);
	}



	private LogTraceInfoDTO getLogTraceInfo() {
		return new LogTraceInfoDTO(
				tracer.currentSpan().context().spanIdString(), 
				tracer.currentSpan().context().traceIdString());
	}

}