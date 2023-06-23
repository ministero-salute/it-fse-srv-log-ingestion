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
