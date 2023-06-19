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

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.enums.RegionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssuerDTO {

    @Field("raw_value")
    private String rawValue;

    @Field("region")
    private String region;

    @Field("asl")
    private String asl;

    @Field("enterprise")
    private String enterprise;

    @Field("fiscal_code")
    private String fiscalCode;

    /**
     * Decode issuer basing on input information
     * 1) certificati regionali: AAA = 3
     * 2) certificati aziendali: AAA BBB = 6
     * 3) certificati per sistema aziendale: AAA BBB Cl = ?
     * 4) Certificato struttura privata Accreditata : AAA BBB Ca = 12
     * 5) Certificato per struttura privata:AAA Cp = 9
     * 6) Certificato per MMG/PLS:AAA BBB CF = 22
     * 7) Certificato da Medico Privato:AAA CF = 19
     *
     * @param issuer The field to be decoded.
     * @return A DTO containing issuer information.
     */
    public static IssuerDTO decodeIssuer(String issuer) {
        IssuerDTO out = new IssuerDTO();
        out.setRawValue(issuer);

        // Issuer Cleaning
        String firstFilter = issuer.substring(issuer.indexOf("#") + 1)
                .replaceAll("\\s+", "");
        String region = firstFilter.substring(0, 3);
        if (RegionEnum.getFromCode(region) != null) {
            out.setRegion(region);
        }

        String subIssuer = firstFilter.substring(3);

        if (subIssuer.isEmpty()) { // 1) certificati regionali: AAA = 3
            return out;
        } else if (Constants.App.Regex.Compiled.COMPILED_ASL_REGEX.matcher(subIssuer).matches()) { // 2) certificati aziendali: AAA BBB = 6
            out.setAsl(subIssuer);
            return out;
        }  else if (Constants.App.Regex.Compiled.COMPILED_PRIVATE_SSN_HOSPITAL_REGEX.matcher(subIssuer).matches()) { // 4) Certificato struttura privata Accreditata : AAA BBB Ca = 12
            out.setAsl(subIssuer.substring(0, 3));
            out.setEnterprise(subIssuer.substring(3));
            return out;
        } else if (Constants.App.Regex.Compiled.COMPILED_PRIVATE_HOSPITAL_REGEX.matcher(subIssuer).matches()) { // 5) Certificato per struttura privata:AAA Cp = 9
            out.setEnterprise(subIssuer);
            return out;
        } else if (Constants.App.Regex.Compiled.COMPILED_PRIVATE_DOCTOR_REGEX.matcher(subIssuer).matches()) { // 7) Certificato da Medico Privato:AAA CF = 19
            out.setFiscalCode(subIssuer);
            return out;
        } else if (Constants.App.Regex.Compiled.COMPILED_MMG_PLS_REGEX.matcher(subIssuer).matches()) { // 6) Certificato per MMG/PLS:AAA BBB CF = 22
            out.setAsl(subIssuer.substring(0,3));
            out.setFiscalCode(subIssuer.substring(3));
            return out;
        }
        else if (Constants.App.Regex.Compiled.COMPILED_COMPLEX_STRUCTURE.matcher(subIssuer).matches()) { // 3) certificati per sistema aziendale: AAA BBB Cl = ?
            out.setAsl(subIssuer.substring(3, 6));
            out.setEnterprise(subIssuer.substring(6));
            return out;
        }
        return out;
    }
}
