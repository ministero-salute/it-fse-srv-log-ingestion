/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.dto.response;

import java.util.List;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogControllerResDTO extends ResponseDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1630931782215900987L;

	private List<LogCollectorETY> entity;
	
	public LogControllerResDTO() {
		super();
	}

	public LogControllerResDTO(final LogTraceInfoDTO traceInfo, final List<LogCollectorETY> inEntity) {
		super(traceInfo);
		entity = inEntity;
	}
	
}