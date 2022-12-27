/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.dto.response;

import java.util.List;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogControllerResDTO {
	

	private List<LogCollectorControlETY> entity;
	
	public LogControllerResDTO() {
		super();
	}

	public LogControllerResDTO(final LogTraceInfoDTO traceInfo, final List<LogCollectorControlETY> inEntity) {
		entity = inEntity;
	}
	
}