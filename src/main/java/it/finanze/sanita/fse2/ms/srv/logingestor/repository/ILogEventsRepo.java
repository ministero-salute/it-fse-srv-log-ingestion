package it.finanze.sanita.fse2.ms.srv.logingestor.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.bson.Document;

public interface ILogEventsRepo extends Serializable {
  
	void saveLogEvent(String json);
	
	List<Document> getLogEvents(String region, Date startDate, Date endDate, String docType);
	
}
