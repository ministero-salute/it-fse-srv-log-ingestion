package it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ILogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class LogEventsRepo implements ILogEventsRepo {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -4017623557412046071L;

	@Autowired
	private transient MongoTemplate mongoTemplate;
	
	@Override
	public void saveLogEvent(final String json) {
		try {
			mongoTemplate.save(new LogCollectorETY(null,Document.parse(json)));
		} catch(Exception ex){
			log.error("Error while save event : " , ex);
			throw new BusinessException("Error while save event : " , ex);
		}
	}
	 
}
