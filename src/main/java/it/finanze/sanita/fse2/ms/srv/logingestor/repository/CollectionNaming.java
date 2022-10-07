package it.finanze.sanita.fse2.ms.srv.logingestor.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.ProfileUtility;

@Configuration
public class CollectionNaming {

    @Autowired
    private ProfileUtility profileUtility;

    @Bean("logCollectorBean")
    public String getLogCollectorCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.LOG_COLLECTION_NAME;
        }
        return Constants.ComponentScan.Collections.LOG_COLLECTION_NAME;
    }
    
}
