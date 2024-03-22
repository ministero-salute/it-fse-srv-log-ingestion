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

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;

@Document(collection = "#{@logCollectorKpiBean}")
public class LogCollectorKpiETY extends LogCollectorBase {

	@Field(name = Constants.Mongo.Fields.IS_SSN)
    @JsonProperty(Constants.Mongo.Fields.IS_SSN)
    private boolean isSsn;

    @Field(name = Constants.Mongo.Fields.REGISTRY)
    @JsonProperty(Constants.Mongo.Fields.REGISTRY)
    private String registry;

    @Field(name = Constants.Mongo.Fields.REGION)
    @JsonProperty(Constants.Mongo.Fields.REGION)
    private String region;

    @Field(name = Constants.Mongo.Fields.COMPANY)
    @JsonProperty(Constants.Mongo.Fields.COMPANY)
    private String company;

    @Field(name = Constants.Mongo.Fields.STRUCTURE)
    @JsonProperty(Constants.Mongo.Fields.STRUCTURE)
    private String structure;
	
}
