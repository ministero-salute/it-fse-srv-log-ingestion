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
package it.finanze.sanita.fse2.ms.srv.logingestor.controller.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.srv.logingestor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.ISearchLogEventsCTL;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogControllerResDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.ILogEventsSRV;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.DateUtility;

@RestController
public class SearchLogEventsCTL extends AbstractCTL implements ISearchLogEventsCTL {

	@Autowired
	private ILogEventsSRV logEventsSrv;

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

		List<LogCollectorControlETY> res = logEventsSrv.getLogEvents(region, startDate, endDate, docType);

		return new LogControllerResDTO(getLogTraceInfo(), res);
	}


}
