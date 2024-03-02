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
package it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.LocalityDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.SubjApplicationDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.JsonUtility;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ILogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorKpiETY;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class LogEventsRepo implements ILogEventsRepo {

	@Autowired
	private MongoTemplate mongoTemplate;
	

	@Override
	public void saveLogEvent(final String json) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
			simpleDateFormat.setTimeZone(TimeZone.getDefault());
			Document doc = buildDocumentToSave(json);

			LogCollectorBase b = null;
			if(Constants.Mongo.Fields.LOG_TYPE_CONTROL.equals(doc.getString("log_type"))) {
				b = JsonUtility.clone(doc, LogCollectorControlETY.class);
			} else {
				b = JsonUtility.clone(doc, LogCollectorKpiETY.class);
			}
			
			mongoTemplate.save(b);
			log.info("Salvataggio su mongo effettuato");
		} catch(Exception ex){
			log.error("Error while save event : " , ex);
			throw new BusinessException("Error while save event : " , ex);
		}
	}

	@Override
	public List<LogCollectorControlETY> getLogEvents(String region, Date startDate, Date endDate, String docType) {
		try {
			Query query = new Query();
			Criteria cri = Criteria.where(Constants.Mongo.Fields.OP_TIMESTAMP_START).gte(startDate).and(Constants.Mongo.Fields.OP_TIMESTAMP_END).lte(endDate);

			if(!StringUtils.isEmpty(region)) {
				cri.and(Constants.Mongo.Query.REGION).is(region);
			} 
			
			if(!StringUtils.isEmpty(docType)){
				cri.and(Constants.Mongo.Fields.OP_DOCUMENT_TYPE).is(docType);
			} 
			query.fields().exclude(Constants.Mongo.Fields.ID);
			query.addCriteria(cri);
			query.limit(100).with(Sort.by(Constants.Mongo.Fields.OP_TIMESTAMP_START).descending());
			return mongoTemplate.find(query, LogCollectorControlETY.class);
		} catch (Exception e) {
			log.error("Error while getting records : " , e);
			throw new BusinessException("Error while getting records : " , e);
		}		
	}
	 
	
	private Document buildDocumentToSave(final String json) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
		Document doc = Document.parse(json);
		String issuer = doc.getString(Constants.Mongo.Fields.OP_ISSUER);
		String locality = doc.getString(Constants.Mongo.Fields.OP_LOCALITY);
		doc.remove(Constants.Mongo.Fields.OP_ISSUER);
		doc.remove(Constants.Mongo.Fields.OP_LOCALITY);
		
		Date startDate = null;
		if (doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_START) != null) {
			startDate = simpleDateFormat.parse(doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_START));
		}
		
		Date endDate = null;
		if (doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_END) != null) {
			endDate = simpleDateFormat.parse(doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_END));
		}
		doc.put(Constants.Mongo.Fields.OP_TIMESTAMP_START, startDate);
		doc.put(Constants.Mongo.Fields.OP_TIMESTAMP_END, endDate);

		if (StringUtils.isNotEmpty(issuer)) {
			IssuerDTO issuerDTO = IssuerDTO.decodeIssuer(issuer);
			doc.put(Constants.Mongo.Fields.OP_ISSUER, Document.parse(JsonUtility.objectToJson(issuerDTO)));
		}

		if (StringUtils.isNotEmpty(locality)) {
			LocalityDTO localityDTO = LocalityDTO.decodeLocality(locality);
			doc.put(Constants.Mongo.Fields.OP_LOCALITY, Document.parse(JsonUtility.objectToJson(localityDTO)));
		}
		
		String subjApplicationId = doc.getString(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_ID);
		String subjApplicationVendor = doc.getString(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VENDOR);
		String subjApplicationVersion = doc.getString(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VERSION);
		doc.remove(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_ID);
		doc.remove(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VENDOR);
		doc.remove(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VERSION);
		
		SubjApplicationDTO subjDTO = new SubjApplicationDTO();
		boolean saveField = false;
		if(StringUtils.isNotEmpty(subjApplicationId)) {
			subjDTO.setSubject_application_id(subjApplicationId);
			saveField = true;
		} 

		if(StringUtils.isNotEmpty(subjApplicationVendor)) {
			subjDTO.setSubject_application_vendor(subjApplicationVendor);
			saveField = true;
		} 
		
		if(StringUtils.isNotEmpty(subjApplicationVersion)) {
			subjDTO.setSubject_application_version(subjApplicationVersion);
			saveField = true;
		}
		if(saveField) {
			doc.put(Constants.Mongo.Fields.OP_SUBJ_APPLICATION, Document.parse(JsonUtility.objectToJson(subjDTO)));
		}
		
		return doc;
	}

	@Override
	public Integer saveLog(List<? extends LogCollectorBase> logs) {
		Integer numInsert = 0;
		try {
			mongoTemplate.insertAll(logs);
			numInsert = logs.size();
			log.info("Salvataggio su mongo effettuato");
		} catch (Exception ex) {
			log.error("Error while save logs : ", ex);
			throw new BusinessException("Error while save logs : ", ex);
		}

		return numInsert;
	}
 
}