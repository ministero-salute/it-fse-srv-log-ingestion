/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.service;

import java.util.Date;
import java.util.List;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;

public interface ILogEventsSRV {

	List<LogCollectorControlETY> getLogEvents(String region, Date startDate, Date endDate, String docType);
}
