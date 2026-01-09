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

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalityDTO {
	
	private static final String STS_OID = "2.16.840.1.113883.2.9.4.1.3";
    
    @Field("raw_value")
    private String rawValue;

    @Field("asl_code")
    private String aslCode;

    @Field("structure")
    private String structure;

    /**
     * Decode locality basing on input information.
     * 
     * @param locality The field to be decoded.
     * @return A DTO containing locality information.
     */ 
    public static LocalityDTO decodeLocality(String locality) {

        LocalityDTO out = new LocalityDTO();
        out.setRawValue(locality);

        if (StringUtils.isEmpty(locality)) {
            return out;
        }

        String[] parts = locality.split("&");

        if (parts.length < 2) {
            return out; // non c'è seconda parte
        }

        String secondPart = parts[1];
        if (!STS_OID.equals(secondPart)) {
            return out;  
        }

        int lastCaretIndex = locality.lastIndexOf("^^^^");
        if (lastCaretIndex == -1) {
            return out;
        }

        String lastToken = locality.substring(lastCaretIndex + 4);

        if (lastToken.length() == 12) {
            out.setAslCode(lastToken.substring(3, 6));
            out.setStructure(lastToken.substring(6, 12));
        } else if (lastToken.length() == 6) {
            out.setAslCode(lastToken.substring(3, 6));
        }

        return out;
    }

}
