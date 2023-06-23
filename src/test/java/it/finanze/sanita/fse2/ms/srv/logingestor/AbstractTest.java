/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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
