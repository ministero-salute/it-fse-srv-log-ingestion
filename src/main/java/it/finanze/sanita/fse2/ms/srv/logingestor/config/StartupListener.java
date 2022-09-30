package it.finanze.sanita.fse2.ms.srv.logingestor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;

@Component
public class StartupListener {

    
    @Autowired
    private MongoTemplate mongoTemplate;
     

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		ensureIndexes();
	}

	private void ensureIndexes() {
		mongoTemplate.indexOps(LogCollectorETY.class).ensureIndex(new Index().on("region", Direction.DESC).background());
	}
	 
}
