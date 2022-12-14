/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.LocalityDTO;
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
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class LogEventsRepo implements ILogEventsRepo {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -4017623557412046071L;

	@Autowired
	private transient MongoTemplate mongoTemplate;
	
	@Override
	public void saveLogEvent(final String json) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
			simpleDateFormat.setTimeZone(TimeZone.getDefault());

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

			log.info("Inizio il clone");
			LogCollectorETY ety = JsonUtility.clone(doc, LogCollectorETY.class);
			mongoTemplate.save(ety);
			log.info("Salvataggio su mongo effettuato");
		} catch(Exception ex){
			log.error("Error while save event : " , ex);
			throw new BusinessException("Error while save event : " , ex);
		}
	}

	@Override
	public List<LogCollectorETY> getLogEvents(String region, Date startDate, Date endDate, String docType) {
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
			return mongoTemplate.find(query, LogCollectorETY.class);
		} catch (Exception e) {
			log.error("Error while getting records : " , e);
			throw new BusinessException("Error while getting records : " , e);
		}		
	}
	
}