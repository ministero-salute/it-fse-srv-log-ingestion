package it.finanze.sanita.fse2.ms.srv.logingestor.service;

import java.util.Date;
import java.util.List;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;

public interface ILogEventsSRV {

	List<LogCollectorETY> getLogEvents(String region, Date startDate, Date endDate, String docType);
}
