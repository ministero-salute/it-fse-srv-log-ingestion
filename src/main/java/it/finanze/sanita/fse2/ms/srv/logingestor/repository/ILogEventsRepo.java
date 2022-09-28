package it.finanze.sanita.fse2.ms.srv.logingestor.repository;

import java.io.Serializable;


public interface ILogEventsRepo extends Serializable {
  
	void saveLogEvent(String json);
	
}
