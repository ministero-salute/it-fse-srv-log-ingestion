/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorKpiETY;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ILogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.JsonUtility;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@Slf4j
class LogEventsTest extends AbstractTest {

	@Autowired
	ILogEventsRepo eventsRepo;

	@Autowired
	MongoTemplate mongoTemplate;

	@ParameterizedTest
	@ValueSource(ints = { 10, 100, 1000 })
	@DisplayName("Save json log control and search it in collection")
	void saveLogControl(final int numEntities) {

		final String json = "{\"op_document_type\":\"value\", \"log_type\": \"control-structured-log\"}";
		for (int i = 0; i < numEntities; i++) {
			eventsRepo.saveLogEvent(json);
		}

		List<LogCollectorControlETY> persistedEntities = mongoTemplate.find(new Query(), LogCollectorControlETY.class);

		assertEquals(numEntities, persistedEntities.size());
		persistedEntities.forEach(entity -> {
			assertNotNull(entity.getId());
		});
	}

	@ParameterizedTest
	@ValueSource(ints = { 10, 100, 1000 })
	@DisplayName("Save json kpi log and search it in collection")
	void saveKpiLog(final int numEntities) {

		final String json = "{\"op_document_type\":\"value\"}";
		for (int i = 0; i < numEntities; i++) {
			eventsRepo.saveLogEvent(json);
		}

		List<LogCollectorKpiETY> persistedEntities = mongoTemplate.find(new Query(), LogCollectorKpiETY.class);

		assertEquals(numEntities, persistedEntities.size());
		persistedEntities.forEach(entity -> {
			assertNotNull(entity.getId());
		});
	}

	@ParameterizedTest
	@CsvSource({ "201123456,07/10/2022,04/10/2022,test1", "201123456,03/02/2022,04/04/2022,test2" })
	@DisplayName("Save Log Events and search it by getLogEvents")
	void getLogEvents(String region, Date startDate, Date endDate, String docType) {

		Document doc = new Document();
		doc.put("op_timestamp_start", startDate);
		doc.put("op_timestamp_end", endDate);
		doc.put("op_document_type", docType);

		IssuerDTO issuerDTO = new IssuerDTO();
		issuerDTO.setRegion(region);
		doc.put("op_issuer", Document.parse(JsonUtility.objectToJson(issuerDTO)));

		LogCollectorControlETY ety = JsonUtility.clone(doc, LogCollectorControlETY.class);

		mongoTemplate.insert(ety);

		List<LogCollectorControlETY> result = eventsRepo.getLogEvents(region, startDate, endDate, docType);

		assertEquals(1, result.size());
		assertEquals(ety.getDocumentType(),result.get(0).getDocumentType());
		assertEquals(ety.getTimestampStart(), result.get(0).getTimestampStart());
		assertEquals(ety.getTimestampEnd(), result.get(0).getTimestampEnd());

		log.info("LOG ETY: " + result.get(0).toString());
	}

	@ParameterizedTest
	@CsvSource({ "201123456,07/10/2022,04/10/2022,test1", "201123456,03/02/2022,04/04/2022,test2" })
	@DisplayName("Save log null and search another log by getLogEvents")
	void getLogEventsWrong(String region, Date startDate, Date endDate, String docType) {

		LogCollectorControlETY ety = new LogCollectorControlETY();

		mongoTemplate.insert(ety);

		List<LogCollectorControlETY> result = eventsRepo.getLogEvents(region, startDate, endDate, docType);

		assertEquals(0, result.size());
	}

	@ParameterizedTest
	@CsvSource({ "201123456,02/10/2022,04/10/2022,test1", "201123456,01/02/2022,04/02/2022,test2" })
	@DisplayName("Save log and search log with startDate greater than endDate")
	void getLogStartGteEnd(String region, Date startDate, Date endDate, String docType) {

		Document doc = new Document();
		doc.put("op_timestamp_start", startDate);
		doc.put("op_timestamp_end", endDate);
		doc.put("op_document_type", docType);

		LogCollectorControlETY ety = JsonUtility.clone(doc, LogCollectorControlETY.class);

		mongoTemplate.insert(ety);
		
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.DATE, 365);
		startDate = c.getTime();

		List<LogCollectorControlETY> result = eventsRepo.getLogEvents(region, startDate, endDate, docType);

		assertEquals(0, result.size());
	}

	@ParameterizedTest
	@CsvSource(value = { "201123456,02/10/2022,04/10/2022,test1" }, nullValues = { "null" })
	@DisplayName("Save log event and search it by null dates")
	void getLogEventNullDate(String region, Date startDate, Date endDate, String docType) {

		Document doc = new Document();
		doc.put("op_timestamp_start", startDate);
		doc.put("op_timestamp_end", endDate);
		doc.put("op_document_type", docType);

		LogCollectorControlETY ety = JsonUtility.clone(doc, LogCollectorControlETY.class);

		mongoTemplate.insert(ety);

		List<LogCollectorControlETY> result = eventsRepo.getLogEvents(region, null, null, docType);

		assertEquals(0, result.size());
	}

}
