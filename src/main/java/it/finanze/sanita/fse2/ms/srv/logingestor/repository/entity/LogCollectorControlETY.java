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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.LocalityDTO;

@Document(collection = "#{@logCollectorControlBean}")
@EqualsAndHashCode(callSuper = false)
@Data
public class LogCollectorControlETY extends LogCollectorBase {
	
	@Field(name = Constants.Mongo.Fields.OP_ERROR)
    @JsonProperty(Constants.Mongo.Fields.OP_ERROR)
    private String error;

    @Field(name = Constants.Mongo.Fields.OP_ERROR_DESCRIPTION)
    @JsonProperty(Constants.Mongo.Fields.OP_ERROR_DESCRIPTION)
    private String errorDescription;

    @Field(name = Constants.Mongo.Fields.OP_ISSUER)
    @JsonProperty(Constants.Mongo.Fields.OP_ISSUER)
    private IssuerDTO issuer;

    @Field(name = Constants.Mongo.Fields.OP_LOCALITY)
    @JsonProperty(Constants.Mongo.Fields.OP_LOCALITY)
    private LocalityDTO locality;

    @Field(name = Constants.Mongo.Fields.GATEWAY_NAME)
    @JsonProperty(Constants.Mongo.Fields.GATEWAY_NAME)
    private String gatewayName;

    @Field(name = Constants.Mongo.Fields.OP_WARNING)
    @JsonProperty(Constants.Mongo.Fields.OP_WARNING)
    private String warning;

    @Field(name = Constants.Mongo.Fields.OP_WARNING_DESCRIPTION)
    @JsonProperty(Constants.Mongo.Fields.OP_WARNING_DESCRIPTION)
    private String warningDescription;

    @Field(name = Constants.Mongo.Fields.TYPE_ID_EXTENSION)
    @JsonProperty(Constants.Mongo.Fields.TYPE_ID_EXTENSION)
    private String typeIdExtension;
	
}
