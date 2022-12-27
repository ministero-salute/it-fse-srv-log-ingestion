/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "#{@logCollectorKpiBean}")
public class LogCollectorKpiETY extends LogCollectorBase {

	
}
