package it.finanze.sanita.fse2.ms.srv.logingestor;

import com.google.gson.Gson;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ILogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorKpiETY;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;

public abstract class AbstractTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ILogEventsRepo eventsRepo;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection(LogCollectorKpiETY.class);
        mongoTemplate.dropCollection(LogCollectorControlETY.class);
    }

    void createKpiLogMockEvents() {
        String logs = new String(FileUtility.getFileFromInternalResources("Files" + File.separator + "kpi_log.json"));
        Document[] docs = new Gson().fromJson(logs, Document[].class);
        for (Document document : docs) {
            eventsRepo.saveLogEvent(new Gson().toJson(document));
        }
    }

    void createControlLogMockEvents() {
        String logs = new String(FileUtility.getFileFromInternalResources("Files" + File.separator + "control_log.json"));
        Document[] docs = new Gson().fromJson(logs, Document[].class);
        for (Document document : docs) {
            eventsRepo.saveLogEvent(new Gson().toJson(document));
        }
    }
}
