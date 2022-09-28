package it.finanze.sanita.fse2.ms.srv.logingestor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ILogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = { Constants.ComponentScan.BASE })
@ActiveProfiles(Constants.Profile.TEST)
class SaveTest {

	@Autowired
	ILogEventsRepo eventsRepo;

	@Autowired
	MongoTemplate mongoTemplate;

	@BeforeEach
	void setup() {
		mongoTemplate.dropCollection(LogCollectorETY.class);
	}

	@ParameterizedTest
	@ValueSource(ints = { 10, 100, 1000 })
	@DisplayName("Save json and search it in collection")
	void save(final int numEntities) {

		final String json = "{\"test\":\"value\"}";
		for (int i = 0; i < numEntities; i++) {
			eventsRepo.saveLogEvent(json);
		}

		List<LogCollectorETY> persistedEntities = mongoTemplate.find(new Query(), LogCollectorETY.class);

		assertEquals(numEntities, persistedEntities.size());
		persistedEntities.forEach(entity -> {
			assertNotNull(entity.getId());
		});
	}

}
