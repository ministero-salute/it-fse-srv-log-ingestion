package it.finanze.sanita.fse2.ms.srv.logingestor.repository;

import java.io.Serializable;


public interface ITransactionEventsRepo extends Serializable {
  
	void saveEvent(String key, String json);
	
}
