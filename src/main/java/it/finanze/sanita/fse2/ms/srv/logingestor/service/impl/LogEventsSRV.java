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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.srv.logingestor.dto.EsitoDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl.LogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;
import it.finanze.sanita.fse2.ms.srv.logingestor.threads.LogThread;
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
	public void srvListenerTest(final String value, int totalDocuments, int numThread) {

		List<LogThread> threads = new ArrayList<>();

		for (int i = 0; i < numThread; i++) {
			LogThread thread = new LogThread(eventsRepo, totalDocuments / numThread, value);
			thread.start();
			log.info("STARTING NEW THREAD");
			threads.add(thread);
		}

		for (LogThread thread : threads) {
			try {
				thread.join();
				log.info("JOINED WITH THREAD " + thread.getName());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

	@Override
	public <T extends LogCollectorBase> EsitoDTO processChunk(List<T> logs) {
		EsitoDTO esito = new EsitoDTO();

		Integer numInsert = 0;

		// Raccoglie i wii dalla lista
		List<String> wiis = logs.stream()
				.map(log -> log.getWorkflowInstanceId())
				.collect(Collectors.toList());

		// Cerco sul DB i log giá salvati
		List<? extends LogCollectorBase> resultList = eventsRepo.findLogsIn(wiis, logs.get(0).getClass());

		// Se non ci sono log salvati sul DB
		if (resultList.isEmpty()) {
			// inserisco tutti
			eventsRepo.insertAll(logs);
		} else {
			List<T> logsToInsert = new ArrayList<>();
			// Creo il dizionario per la ricerca
			Map<String, List<String>> dict = new HashMap<>();
			for (int i = 0; i < resultList.size(); i++) {
				String wii = resultList.get(i).getWorkflowInstanceId();
				String operation = resultList.get(i).getOperation();
				if (dict.containsKey(wii)) {
					List<String> ops = dict.get(wii);
					ops.add(operation);
					dict.put(wii, ops);
				} else {
					List<String> temp = Arrays.asList(operation);
					dict.put(wii, temp);
				}
			}
			// Per ogni log
			for (int i = 0; i < logs.size(); i++) {
				// Cerco se esiste nei log giá presenti
				String wii = logs.get(i).getWorkflowInstanceId();
				String operation = logs.get(i).getOperation();
				if (dict.get(wii) == null || !dict.get(wii).contains(operation)) {
					logsToInsert.add(logs.get(i));
				}
			}
			eventsRepo.insertAll(logsToInsert);
		}

		numInsert = logs.size();
		log.info("Salvataggio su mongo effettuato");
		esito.setNumInsert(numInsert);
		return esito;

	}
}
