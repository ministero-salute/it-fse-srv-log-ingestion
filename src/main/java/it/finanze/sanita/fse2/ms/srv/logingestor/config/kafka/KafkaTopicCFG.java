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
package it.finanze.sanita.fse2.ms.srv.logingestor.config.kafka;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.ProfileUtility;
import lombok.Data;


@Data
@Component
public class KafkaTopicCFG {

	@Autowired
	private ProfileUtility profileUtility;
 
	/**
	 * Log Ingestor Dead letter Topic. 
	 */
	@Value("${kafka.srv-log-ingestor.deadletter.topic}")
	private String logIngestorDeadLetterTopic;
	

	@Value("${kafka.log.base-topic}")
	private String logTopic;
	
	/**
	 * Log Ingestor Dead letter Topic. 
	 */
	@Value("${kafka.srv-log-ingestor-eds.deadletter.topic}")
	private String logIngestorDeadLetterTopicEds;
	

	@Value("${kafka.log.base-eds-topic}")
	private String logTopicEds;


	@PostConstruct
	public void afterInit() {
		if (profileUtility.isTestProfile()) {
			this.logTopic = Constants.Profile.TEST_PREFIX + this.logTopic;
			this.logIngestorDeadLetterTopic = Constants.Profile.TEST_PREFIX + this.logIngestorDeadLetterTopic;
		}
	}

}
