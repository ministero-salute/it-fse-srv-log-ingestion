package it.finanze.sanita.fse2.ms.srv.logingestor.config.kafka;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.ProfileUtility;
import lombok.Data;

/**
 *	@author vincenzoingenito
 *
 *	Kafka topic configuration.
 */
@Data
@Component
public class KafkaTopicCFG {

	@Autowired
	private ProfileUtility profileUtility;

	/**
	 * Log Ingestor Topic.
	 */
	@Value("${kafka.srv-log-ingestor.topic}")
	private String logIngestorTopic;
	
	/**
	 * Log Ingestor Dead letter Topic. 
	 */
	@Value("${kafka.srv-log-ingestor.deadletter.topic}")
	private String logIngestorDeadLetterTopic;
	

	@Value("${kafka.log.base-topic}")
	private String logTopic;


	@PostConstruct
	public void afterInit() {
		if (profileUtility.isTestProfile()) {
			this.logIngestorTopic = Constants.Profile.TEST_PREFIX + this.logIngestorTopic;
			this.logIngestorDeadLetterTopic = Constants.Profile.TEST_PREFIX + this.logIngestorDeadLetterTopic;
		}
	}

}
