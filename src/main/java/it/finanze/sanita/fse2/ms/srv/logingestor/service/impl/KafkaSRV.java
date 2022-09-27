package it.finanze.sanita.fse2.ms.srv.logingestor.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ITransactionEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.KafkaAbstractSRV;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vincenzoingenito
 *
 *         Kafka management service.
 */
@Service
@Slf4j
public class KafkaSRV extends KafkaAbstractSRV implements IKafkaSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 987723954716001270L;
	
	@Autowired
	private ITransactionEventsRepo eventsRepo;

	@Override
	@KafkaListener(topics = "#{'${kafka.srv-log-ingestor.topic}'}", clientIdPrefix = "#{'${kafka.consumer.client-id}'}", containerFactory = "kafkaListenerIngestorContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id}'}")
	public void listener(final ConsumerRecord<String, String> cr, final MessageHeaders messageHeaders) {
		try {
			String key = cr.key();
			String value = cr.value();
			log.info("Consuming Transaction Event - Message received with key {}", key);
			log.info("Consuming Transaction Event - Message received with value {}", value);
			srvListener(key, value);
		} catch (Exception e) {
			deadLetterHelper(e);
			throw new BusinessException(e);
		}
	}

	public void srvListener(final String key, final String value) {
		eventsRepo.saveEvent(key, value);
	}

	/**
	 * @param e
	 */
	private void deadLetterHelper(Exception e) {
		StringBuilder sb = new StringBuilder(
				"LIST OF USEFUL EXCEPTIONS TO MOVE TO DEADLETTER OFFSET 'kafka.consumer.dead-letter-exc'. ");
		boolean continua = true;
		Throwable excTmp = e;
		Throwable excNext = null;

		while (continua) {

			if (excNext != null) {
				excTmp = excNext;
				sb.append(", ");
			}

			sb.append(excTmp.getClass().getCanonicalName());
			excNext = excTmp.getCause();

			if (excNext == null) {
				continua = false;
			}

		}

		log.error("{}", sb);
	}

	@Override
	public void sendLoggerStatus(String log) {
		sendMessage(kafkaTopicCFG.getLogTopic(), "KEY", log, true);
	}

}
