/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.service;

import java.io.Serializable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.MessageHeaders;
 

public interface IKafkaSRV extends Serializable {

	void listenerGtwTopic(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders);
	
	void listenerEdsTopic(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders);

}
