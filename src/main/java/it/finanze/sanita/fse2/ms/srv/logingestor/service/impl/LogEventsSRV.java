/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl.LogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogEventsSRV implements ILogEventsSRV {
	
	@Autowired
	private LogEventsRepo eventsRepo;
	
	@Override
	public List<LogCollectorETY> getLogEvents(String region, Date startDate, Date endDate, String docType) {
		List<LogCollectorETY> out = new ArrayList<>();
		
		try {
			out = eventsRepo.getLogEvents(region, startDate, endDate, docType);
		} catch (Exception e) {
			log.error("Error while getting log events: ", e);
			throw new BusinessException("Error while getting log events: ", e);
		}
		return out;
	}
	
}
