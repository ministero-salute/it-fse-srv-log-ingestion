/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ILogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.IKafkaSRV;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class KafkaSRV implements IKafkaSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 987723954716001270L;
	
	@Autowired
	private transient ILogEventsRepo eventsRepo;

	@Override
	@KafkaListener(topics = "#{'${kafka.log.base-topic}'}", clientIdPrefix = "#{'${kafka.consumer.client-id}'}", containerFactory = "kafkaListenerIngestorContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id}'}")
	public void listenerGtwTopic(final ConsumerRecord<String, String> cr, final MessageHeaders messageHeaders) {
		try {
			log.info("Sono il consumer del base-topic e consumo il msg: "+ cr.value());
			log.debug("Consuming Transaction Event - Message received with value {}", cr.value());
			srvListener(cr.value());
		} catch (Exception e) {
			deadLetterHelper(e);
			throw new BusinessException(e);
		}
	}
	
	@Override
	@KafkaListener(topics = "#{'${kafka.log.base-eds-topic}'}", clientIdPrefix = "#{'${kafka.consumer-eds.client-id}'}", containerFactory = "kafkaListenerIngestorEdsContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id}'}")
	public void listenerEdsTopic(final ConsumerRecord<String, String> cr, final MessageHeaders messageHeaders) {
		try {
			log.info("Sono il consumer del base-eds-topic e consumo il msg: "+ cr.value());
			log.debug("Consuming Transaction Event - Message received with value {}", cr.value());
			srvListener(cr.value());
		} catch (Exception e) {
			deadLetterHelper(e);
			throw new BusinessException(e);
		}
	}

	private void srvListener(final String value) {
		eventsRepo.saveLogEvent(value);
	}

	/**
	 * @param e
	 */
	private void deadLetterHelper(Exception e) {
		StringBuilder sb = new StringBuilder("LIST OF USEFUL EXCEPTIONS TO MOVE TO DEADLETTER OFFSET 'kafka.consumer.dead-letter-exc'. ");
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
 
}
