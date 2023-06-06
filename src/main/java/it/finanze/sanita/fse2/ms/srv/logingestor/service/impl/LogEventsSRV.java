/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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

	@Override
	public void srvListenerTest(final String value,int totalDocuments,int numThread) {
	    int batchSize = 1000; // Dimensione del batch

	    ExecutorService executor = Executors.newFixedThreadPool(numThread);
	    List<CompletableFuture<Void>> futures = new ArrayList<>();

	    int batchCount = totalDocuments / batchSize;
	    int remainingDocuments = totalDocuments % batchSize;

	    for (int i = 0; i < batchCount; i++) {
	        int startIndex = i * batchSize;
	        int endIndex = startIndex + batchSize;
	        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
	            createLogBatch(value, startIndex, endIndex);
	        }, executor);
	        futures.add(future);
	    }

	    if (remainingDocuments > 0) {
	        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
	            int startIndex = batchCount * batchSize;
	            int endIndex = startIndex + remainingDocuments;
	            createLogBatch(value, startIndex, endIndex);
	        }, executor);
	        futures.add(future);
	    }

//	    CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

//	    try {
//	        allFutures.get(10, TimeUnit.MINUTES); // Attendi il completamento di tutte le operazioni entro 10 minuti
//	    } catch (Exception e) {
//	        // Gestisci eventuali eccezioni
//	    }

	    executor.shutdown();

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
