/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;

@Document(collection = "#{@logCollectorControlBean}")
@EqualsAndHashCode(callSuper = false)
public class LogCollectorControlETY extends LogCollectorBase {
	
	@Field(name = Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID)
	@JsonProperty(Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID)
	private String workflowInstanceId;
	
	@Field(name = Constants.Mongo.Fields.TYPE_ID_EXTENSION)
	@JsonProperty(Constants.Mongo.Fields.TYPE_ID_EXTENSION)
	private String typeIdExtension;
	
}
