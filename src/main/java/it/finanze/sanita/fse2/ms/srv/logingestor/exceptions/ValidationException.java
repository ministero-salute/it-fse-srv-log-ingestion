/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.exceptions;

import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.ErrorResponseDTO;
import lombok.Getter;

/**
 * 
 * Validation exeception.
 *
 */
public class ValidationException extends RuntimeException {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 4554229308888951202L;
	
	@Getter
	private ErrorResponseDTO error;
	/**
	 * Message constructor.
	 * 
	 * @param msg	Message to be shown.
	 */
	public ValidationException(final String msg) {
		super(msg);
	}

	public ValidationException(final ErrorResponseDTO inError) {
		error = inError;
	}
	
	/**
	 * Complete constructor.
	 * 
	 * @param msg	Message to be shown.
	 * @param e		Exception to be shown.
	 */
	public ValidationException(final String msg, final Exception e) {
		super(msg, e);
	}
	
	/**
	 * Exception constructor.
	 * 
	 * @param e	Exception to be shown.
	 */
	public ValidationException(final Exception e) {
		super(e);
	}
	
}
