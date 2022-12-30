/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor;

import com.google.gson.Gson;
import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorKpiETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.IKafkaSRV;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Description;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@EmbeddedKafka
class KafkaTest extends AbstractTest {
	
	@SpyBean
	private IKafkaSRV kafkaSRV;

	@Autowired
	private KafkaTopicCFG kafkaTopicCFG;

	@SpyBean
	private RestTemplate restTemplate;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("notxkafkadeadtemplate")
	KafkaTemplate<Object, Object> deadLetterKafkaTemplate;

	@Test
	@Description("Kpi Log topic - Success")
	void kafkaListenerKpiLogSuccessTest() {
		String gtwLogTopic = kafkaTopicCFG.getLogTopic();

		Map<String, Object> map = new HashMap<>();
		MessageHeaders headers = new MessageHeaders(map);
		Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new LinkedHashMap<>();
		records.put(new TopicPartition(gtwLogTopic, 0), new ArrayList<>());

		String logs = new String(FileUtility.getFileFromInternalResources("Files" + File.separator + "kpi_log.json"));

		Document[] docs = new Gson().fromJson(logs, Document[].class);

		final String kafkaValue1 = new Gson().toJson(docs[0]);
		final String kafkaValue2 = new Gson().toJson(docs[1]);
		final String kafkaValue3 = new Gson().toJson(docs[2]);
		final String kafkaValue4 = new Gson().toJson(docs[3]);

		ConsumerRecord<String, String> record1 = new ConsumerRecord<>(gtwLogTopic,
				1, 0, gtwLogTopic, kafkaValue1);
		ConsumerRecord<String, String> record2 = new ConsumerRecord<>(gtwLogTopic,
				1, 0, gtwLogTopic, kafkaValue2);
		ConsumerRecord<String, String> record3 = new ConsumerRecord<>(gtwLogTopic,
				1, 0, gtwLogTopic, kafkaValue3);
		ConsumerRecord<String, String> record4 = new ConsumerRecord<>(gtwLogTopic,
				1, 0, gtwLogTopic, kafkaValue4);

		assertAll(
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerGtwTopic(record1, headers)),
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerGtwTopic(record2, headers)),
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerGtwTopic(record3, headers)),
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerGtwTopic(record4, headers))
		);


		List<LogCollectorKpiETY> persistedEntities = mongoTemplate.find(new Query(), LogCollectorKpiETY.class);

		assertEquals(4, persistedEntities.size());
		persistedEntities.forEach(entity -> {
			assertNotNull(entity.getId());
		});
	}

	@Test
	@Description("Kpi Log topic - Failure to decode issuer")
	void kafkaListenerKpiLogFailureDecodeIssuerTest() {
		String gtwLogTopic = kafkaTopicCFG.getLogTopic();

		Map<String, Object> map = new HashMap<>();
		MessageHeaders headers = new MessageHeaders(map);
		Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new LinkedHashMap<>();
		records.put(new TopicPartition(gtwLogTopic, 0), new ArrayList<>());

		ConsumerRecord<String, String> record1 = new ConsumerRecord<>(gtwLogTopic,
				1, 0, gtwLogTopic, null);

		assertThrows(BusinessException.class, () -> kafkaSRV.listenerGtwTopic(record1, headers));

		List<LogCollectorKpiETY> persistedEntities = mongoTemplate.find(new Query(), LogCollectorKpiETY.class);

		assertEquals(0, persistedEntities.size());
	}

	@Test
	@Description("Control Log topic - Success")
	void kafkaListenerControlLogSuccessTest() {
		String edsLogTopic = kafkaTopicCFG.getLogTopicEds();

		Map<String, Object> map = new HashMap<>();
		MessageHeaders headers = new MessageHeaders(map);
		Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new LinkedHashMap<>();
		records.put(new TopicPartition(edsLogTopic, 0), new ArrayList<>());

		String logs = new String(FileUtility.getFileFromInternalResources("Files" + File.separator + "control_log.json"));

		Document[] docs = new Gson().fromJson(logs, Document[].class);

		final String kafkaValue1 = new Gson().toJson(docs[0]);
		final String kafkaValue2 = new Gson().toJson(docs[1]);
		final String kafkaValue3 = new Gson().toJson(docs[2]);
		final String kafkaValue4 = new Gson().toJson(docs[3]);

		ConsumerRecord<String, String> record1 = new ConsumerRecord<>(edsLogTopic,
				1, 0, edsLogTopic, kafkaValue1);
		ConsumerRecord<String, String> record2 = new ConsumerRecord<>(edsLogTopic,
				1, 0, edsLogTopic, kafkaValue2);
		ConsumerRecord<String, String> record3 = new ConsumerRecord<>(edsLogTopic,
				1, 0, edsLogTopic, kafkaValue3);
		ConsumerRecord<String, String> record4 = new ConsumerRecord<>(edsLogTopic,
				1, 0, edsLogTopic, kafkaValue4);

		assertAll(
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerEdsTopic(record1, headers)),
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerEdsTopic(record2, headers)),
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerEdsTopic(record3, headers)),
				() -> assertDoesNotThrow(() -> kafkaSRV.listenerEdsTopic(record4, headers))
		);


		List<LogCollectorControlETY> persistedEntities = mongoTemplate.find(new Query(), LogCollectorControlETY.class);

		assertEquals(4, persistedEntities.size());
		persistedEntities.forEach(entity -> {
			assertNotNull(entity.getId());
		});
	}
}
