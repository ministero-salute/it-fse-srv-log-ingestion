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
package it.finanze.sanita.fse2.ms.srv.logingestor.dto;

import org.springframework.data.mongodb.core.mapping.Field;

import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class IssuerDTO {

	@Field("raw_value")
	private String rawValue;

	@Field("region")
	private String region;

	@Field("asl")
	private String asl;

	//Il campo enterprise si riferisce alla STRUTTURA e non all'ASL
	@Field("enterprise")
	private String enterprise;

	@Field("fiscal_code")
	private String fiscalCode;


	public static IssuerDTO decodeIssuer(String issuer) {
	    IssuerDTO out = new IssuerDTO();
	    out.setRawValue(issuer);

	    try {
	        int first  = issuer.indexOf('#');
	        int second = issuer.indexOf('#', first + 1);

	        if (first == -1 || second == -1 || first == second) {
	            throw new BusinessException("Input non valido: attesi almeno due '#' — input: " + issuer);
	        }

	        String body = issuer.substring(first + 1, second);

	        if (body.length() < 3) return out;
	        out.setRegion(body.substring(0, 3));

	        if (body.length() < 6) return out;
	        out.setAsl(body.substring(3, 6));

	        if (body.length() < 12) return out;
	        out.setEnterprise(body.substring(6, 12));

	    } catch (Exception ex) {
	        // L'eccezione è volutamente soppressa poiché è NECESSARIO scrivere sempre il raw_value,
	        // altrimenti ci potremmo perdere la loggata
	        log.error("Errore durante il decode:", ex);
	    }

	    return out;
	}
}
