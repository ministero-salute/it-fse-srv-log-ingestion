package it.finanze.sanita.fse2.ms.srv.logingestor.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.srv.logingestor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.ILogCollectorCTL;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.JsonUtility;

@RestController
public class LogCollectorCTL extends AbstractCTL implements ILogCollectorCTL {

	@Autowired
	private ILogEventsSRV logEventSRV;

	@Override
	public ResponseDTO createLogEvents(String logJson) {
		JsonUtility.validateJson(logJson, Object.class);
		logEventSRV.srvListener(logJson);
		return new ResponseDTO(getLogTraceInfo());
	}

	@Override
	public ResponseDTO createLogEventsDataPrep(Integer numDocumenti, Integer numThread, String logJson) {
		JsonUtility.validateJson(logJson, Object.class);
		logEventSRV.srvListenerTest(logJson,numDocumenti, numThread);
		return new ResponseDTO(getLogTraceInfo());
	}

}
