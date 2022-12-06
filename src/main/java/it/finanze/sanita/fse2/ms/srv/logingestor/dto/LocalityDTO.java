/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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

        if (StringUtils.isNotEmpty(locality)) {
            if (locality.length() >= 3) {
                String aslCode = locality.substring(0, 3);
                out.setAslCode(aslCode);
            }
    
            if (locality.length() > 3) {
                String subLocality = locality.substring(3);
                out.setStructure(subLocality);
            }
        }

        return out;
    }
}
