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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
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

//	@Override
//	public void srvListenerTest(final String value,int totalDocuments,int numThread) {
//	    int batchSize = 1000; // Dimensione del batch
//
//	    ExecutorService executor = Executors.newFixedThreadPool(numThread);
//	    List<CompletableFuture<Void>> futures = new ArrayList<>();
//
//	    int batchCount = totalDocuments / batchSize;
//	    int remainingDocuments = totalDocuments % batchSize;
//
//	    for (int i = 0; i < batchCount; i++) {
//	        int startIndex = i * batchSize;
//	        int endIndex = startIndex + batchSize;
//	        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//	            createLogBatch(value, startIndex, endIndex);
//	        }, executor);
//	        futures.add(future);
//	    }
//
//	    if (remainingDocuments > 0) {
//	        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//	            int startIndex = batchCount * batchSize;
//	            int endIndex = startIndex + remainingDocuments;
//	            createLogBatch(value, startIndex, endIndex);
//	        }, executor);
//	        futures.add(future);
//	    }
//
////	    CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//
////	    try {
////	        allFutures.get(10, TimeUnit.MINUTES); // Attendi il completamento di tutte le operazioni entro 10 minuti
////	    } catch (Exception e) {
////	        // Gestisci eventuali eccezioni
////	    }
//
//	    executor.shutdown();
//
//	}
	
	@Override
	public void srvListenerTest(final String value,int totalDocuments,int numThread) {

		for(int i=0; i<totalDocuments; i++) {
			eventsRepo.saveLogEvent(value);
		}

	}

	private void createLogBatch(String logJson, int startIndex, int endIndex) {
	    for (int i = startIndex; i < endIndex; i++) {
//	        String clonedLogJson = cloneLogJson(logJson);
	        eventsRepo.saveLogEvent(logJson);
	    }
	}
	
	
	private String cloneLogJson(String jsonString) {
		String out = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode originalJson = mapper.readTree(jsonString);
			JsonNode clonedJson = originalJson.deepCopy();
			
			String newMessage = "This is the new message";
			((ObjectNode) clonedJson).put("message", newMessage);
			out = mapper.writeValueAsString(clonedJson);
		} catch(Exception ex) {
			System.out.println("Stop");
		}
		return out;

	} 
}
