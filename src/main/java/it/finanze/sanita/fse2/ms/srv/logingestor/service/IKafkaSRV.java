package it.finanze.sanita.fse2.ms.srv.logingestor.service;

import java.io.Serializable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.MessageHeaders;
 

public interface IKafkaSRV extends Serializable {

	public void listener(final ConsumerRecord<String, String> cr, final MessageHeaders messageHeaders);
	
	void sendLoggerStatus(String log);

}
