/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.controller.impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.ISearchLogEventsCTL;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogControllerResDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@Tag(name = "Log Controller")
public class SearchLogEventsCTL extends AbstractCTL implements ISearchLogEventsCTL {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -293930001283648727L;

	@Autowired
	private transient ILogEventsSRV logEventsSrv;

	@Override
	public LogControllerResDTO getLogEvents(String region, Date startDate, Date endDate, String docType) {
		if (startDate==null || endDate==null) {
			throw new ValidationException("Attenzione: valorizzare correttamente startDate e endDate");
		}

		if (startDate.after(endDate)) {
			throw new ValidationException("Data start maggiore di data end");
		}

		startDate = DateUtility.setHoursToDate(startDate, 0, 0, 0);
		endDate = DateUtility.setHoursToDate(endDate, 23,59,59);

		List<LogCollectorETY> res = logEventsSrv.getLogEvents(region, startDate, endDate, docType);

		return new LogControllerResDTO(getLogTraceInfo(), res);
	}


}
