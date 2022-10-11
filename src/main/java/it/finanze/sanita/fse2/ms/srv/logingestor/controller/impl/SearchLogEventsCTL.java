package it.finanze.sanita.fse2.ms.srv.logingestor.controller.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.ISearchLogEventsCTL;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogControllerResDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.DateUtility;

@RestController
@Tag(name = "Log Controller")
public class SearchLogEventsCTL extends AbstractCTL implements ISearchLogEventsCTL {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -293930001283648727L;

	@Autowired
	private ILogEventsSRV logEventsSrv;

	@Override
	public LogControllerResDTO getLogEvents(String region, Date startDate, Date endDate, String docType) {
		List<Document> res = new ArrayList<>();		

		if(startDate.after(endDate)) {
			throw new ValidationException("Data start maggiore di data end");
		}

		startDate = DateUtility.setHoursToDate(startDate, 00,00,00);
		endDate = DateUtility.setHoursToDate(endDate, 23,59,59);

		res = logEventsSrv.getLogEvents(region, startDate, endDate, docType);

		return new LogControllerResDTO(getLogTraceInfo(), res);
	}


}
