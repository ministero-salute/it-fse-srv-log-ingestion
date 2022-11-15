package it.finanze.sanita.fse2.ms.srv.logingestor.dto;

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

    @Field("hospital")
    private String hospital;

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
     * @param issuer
     * @return
     */
    public static IssuerDTO decodeIssuer(String issuer) {
        IssuerDTO out = new IssuerDTO();
        out.setRawValue(issuer);

        // Issuer Cleaning
        String firstFilter = issuer.substring(issuer.indexOf("#") + 1)
                .replaceAll("\\s+", "");
        out.setRegion(firstFilter.substring(0, 3));
        String subIssuer = firstFilter.substring(3);

        if (subIssuer.isEmpty()) {
            // 1) certificati regionali: AAA = 3
            return out;
        } else if (subIssuer.matches("^[0-9]{3}^")) {
            // 2) certificati aziendali: AAA BBB = 6
            out.setAsl(subIssuer);
            return out;
        }  else if (subIssuer.matches("^[0-9]{9}$")) {
            // 4) Certificato struttura privata Accreditata : AAA BBB Ca = 12
            out.setAsl(subIssuer.substring(0, 3));
            out.setHospital(subIssuer.substring(3));
            return out;
        } else if (subIssuer.matches("^[0-9]{6}$")) {
            // 5) Certificato per struttura privata:AAA Cp = 9
            out.setHospital(subIssuer);
            return out;
        } else if (subIssuer.matches("^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$")) {
            // 7) Certificato da Medico Privato:AAA CF = 19
            out.setFiscalCode(subIssuer);
            return out;
        } else if (subIssuer.matches("^[0-9]{3}.*[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$")) {
            // 6) Certificato per MMG/PLS:AAA BBB CF = 22
            out.setAsl(subIssuer.substring(0,3));
            out.setFiscalCode(subIssuer.substring(3));
            return out;
        }
        else if (subIssuer.matches("^[0-9]{3}.*[azA-z0-9]+$")) {
            // 3) certificati per sistema aziendale: AAA BBB Cl = ?
            out.setAsl(subIssuer.substring(3, 6));
            out.setHospital(subIssuer.substring(6));
            return out;
        }
        return out;
    }
}
