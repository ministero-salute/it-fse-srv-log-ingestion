package it.finanze.sanita.fse2.ms.srv.logingestor.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;

public interface ILogEventsRepo extends Serializable {
  
	void saveLogEvent(String json);
	
	List<LogCollectorETY> getLogEvents(String region, Date startDate, Date endDate, String docType);
	
}
