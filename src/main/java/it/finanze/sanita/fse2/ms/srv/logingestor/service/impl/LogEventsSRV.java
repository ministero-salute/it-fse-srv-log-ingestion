/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.ChunkDto;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.EsitoDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorKpiETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl.LogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogEventsSRV implements ILogEventsSRV {

	@Autowired
	private LogEventsRepo eventsRepo;

	@Override
	public List<LogCollectorControlETY> getLogEvents(String region, Date startDate, Date endDate, String docType) {
		List<LogCollectorControlETY> out = new ArrayList<>();

		try {
			out = eventsRepo.getLogEvents(region, startDate, endDate, docType);
		} catch (Exception e) {
			log.error("Error while getting log events: ", e);
			throw new BusinessException("Error while getting log events: ", e);
		}
		return out;
	}

	@Override
	public void srvListener(final String value) {
		eventsRepo.saveLogEvent(value);
	} 
 
	@Override
	public void srvListenerTest(final String value,int totalDocuments,int numThread) {
		for(int i=0; i<totalDocuments; i++) {
			eventsRepo.saveLogEvent(value);
		}
	}

	@Override
	public EsitoDTO processChunk(ChunkDto chunkDto) {
		EsitoDTO esito = new EsitoDTO();

		if (chunkDto.getLogType().equals(Constants.Mongo.Fields.LOG_TYPE_CONTROL)) {
			List<LogCollectorControlETY> controllList = chunkDto.getControllRecord();
			Integer numInsert = eventsRepo.saveLog(controllList);
			esito.setNumInsert(numInsert);
		} else if (chunkDto.getLogType().equals(Constants.Mongo.Fields.LOG_TYPE_KPI)) {
			List<LogCollectorKpiETY> kpiList = chunkDto.getKpiRecord();
			Integer numInsert = eventsRepo.saveLog(kpiList);
			esito.setNumInsert(numInsert);
		}

		return esito;

	}
}
