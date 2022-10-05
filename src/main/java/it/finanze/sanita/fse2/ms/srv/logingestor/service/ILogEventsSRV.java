package it.finanze.sanita.fse2.ms.srv.logingestor.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.bson.Document;

public interface ILogEventsSRV {
	List<Document> getLogEvents(String region, Date startDate, Date endDate, String docType);
}
