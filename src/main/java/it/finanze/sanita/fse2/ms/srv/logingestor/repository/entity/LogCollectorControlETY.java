/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;

@Document(collection = "#{@logCollectorControlBean}")
public class LogCollectorControlETY extends LogCollectorBase {
	
	@Field(name = Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID)
	@JsonProperty(Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID)
	private String workflowInstanceId;
	
}
