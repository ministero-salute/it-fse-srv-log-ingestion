package it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.StringUtility;
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
	
	private static final String pattern = "dd-MM-yyyy HH:mm:ss.SSS";
	
	@Override
	public void saveLogEvent(final String json) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			simpleDateFormat.setTimeZone(TimeZone.getDefault());
			
			Document doc = Document.parse(json);
			String issuer = doc.getString("op_issuer");
			
			Date startDate = null;
			if(doc.getString("op_timestamp_start")!=null) {
				startDate = simpleDateFormat.parse(doc.getString("op_timestamp_start")); 
			}
			
			Date endDate = null;
			if(doc.getString("op_timestamp_end")!=null) {
				endDate = simpleDateFormat.parse(doc.getString("op_timestamp_end")); 
			}
			doc.put("op_timestamp_start", startDate);
			doc.put("op_timestamp_end", endDate);
			
			String region = issuer;
			if(!StringUtils.isEmpty(issuer)) {
				int indexSplit = issuer.indexOf("#");
				if(indexSplit!=-1) {
					region = issuer.substring(indexSplit+1, indexSplit+4);
				} 
			}
			mongoTemplate.save(new LogCollectorETY(null,region,doc));
		} catch(Exception ex){
			log.error("Error while save event : " , ex);
			throw new BusinessException("Error while save event : " , ex);
		}
	}

	@Override
	public List<LogCollectorETY> getLogEvents(String region, Date startDate, Date endDate, String docType) {
		List<LogCollectorETY> out = null;
		try {
			Query query = new Query();
			Criteria cri = new Criteria();
			
			cri.andOperator(Criteria.where("document.op_timestamp_start").gte(startDate).and("document.op_timestamp_end").lte(endDate));
			if(!StringUtility.isNullOrEmpty(region)) {
				cri.and("region").is(region);
			} 
			
			if(!StringUtility.isNullOrEmpty(docType)){
				cri.and("document.op_document_type").is(docType);
			} 
			query.fields().exclude("_id");
			query.addCriteria(cri);
			query.limit(100).with(Sort.by("document.op_timestamp_start").descending());
			out = mongoTemplate.find(query, LogCollectorETY.class);
		} catch (Exception e) {
			log.error("Error while getting records : " , e);
			throw new BusinessException("Error while getting records : " , e);
		}		
		return out;
	}
	
}