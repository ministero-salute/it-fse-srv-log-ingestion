package it.finanze.sanita.fse2.ms.srv.logingestor.controller.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.IControllerTest;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogControllerResDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;

@RestController
@Tag(name = "Log Controller")
public class ControllerTest extends AbstractCTL implements IControllerTest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -293930001283648727L;
	
	@Autowired
	ILogEventsSRV logEventsSrv;

	@Override
	public LogControllerResDTO getLogEvents(String region, Date startDate, Date endDate, String docType) {
		List<Document> res = new ArrayList<>();		
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();		
		int result = startDate.compareTo(endDate);
		
		try {
			if(result==0 || result<0){
				res = logEventsSrv.getLogEvents(region, startDate, endDate, docType);
			}
			else {
				throw new IllegalArgumentException("Error while putting date values");
			}
		} catch (Exception e) {
			throw new BusinessException("Error while getting events : " , e);
		}
		return new LogControllerResDTO(traceInfoDTO, res);
	}

}
