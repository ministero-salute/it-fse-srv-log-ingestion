/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.srv.logingestor.isolation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.enums.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.JsonUtility;

@Tag("ISOLATION")
class UtilitiesIsolationTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid", "one#", "#12#", "#12012#", "#12012345678#", "#120123456789#"})
    void issuerDecodingRetainsRawValueAndOnlyCompleteSegments(String raw) {
        IssuerDTO result = IssuerDTO.decodeIssuer(raw);
        assertEquals(raw, result.getRawValue());
        String body = raw != null && raw.startsWith("#") && raw.endsWith("#") && raw.length() > 1
                ? raw.substring(1, raw.length() - 1) : "";
        assertEquals(body.length() >= 3 ? "120" : null, result.getRegion());
        assertEquals(body.length() >= 6 ? "123" : null, result.getAsl());
        assertEquals(body.length() >= 12 ? "456789" : null, result.getEnterprise());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"missing", "a&wrong&ISO^^^^120123456789", "a&2.16.840.1.113883.2.9.4.1.3&ISO",
            "a&2.16.840.1.113883.2.9.4.1.3&ISO^^^^12"})
    void malformedLocalityRetainsRawValueWithoutInventingCodes(String raw) {
        LocalityDTO result = LocalityDTO.decodeLocality(raw);
        assertEquals(raw, result.getRawValue());
        assertNull(result.getAslCode());
        assertNull(result.getStructure());
    }

    @Test
    void localitySupportsAslAndStructureFormats() {
        String prefix = "x&2.16.840.1.113883.2.9.4.1.3&ISO^^^^";
        LocalityDTO asl = LocalityDTO.decodeLocality(prefix + "120123");
        assertEquals("123", asl.getAslCode());
        assertNull(asl.getStructure());
        LocalityDTO structure = LocalityDTO.decodeLocality(prefix + "120123456789");
        assertEquals("123", structure.getAslCode());
        assertEquals("456789", structure.getStructure());
    }

    @Test
    void jsonConversionHandlesValidInvalidAndUnserializablePayloads() {
        assertEquals("{\"a\":1}", JsonUtility.objectToJson(Map.of("a", 1)));
        assertEquals(1, JsonUtility.jsonToObject("{\"a\":1}", Map.class).get("a"));
        assertEquals("120", JsonUtility.clone(Map.of("region", "120"), IssuerDTO.class).getRegion());
        assertNull(JsonUtility.jsonToObject("not-json", Map.class));
        assertThrows(ValidationException.class, () -> JsonUtility.validateJson("{", Object.class));
        assertEquals(Map.of("a", true), JsonUtility.validateJson("{\"a\":true}", Map.class));
        assertEquals("", JsonUtility.objectToJson(new UnserializablePayload()));
    }

    public static class UnserializablePayload {
        public String getValue() { throw new IllegalStateException("cannot read payload"); }
    }

    @Test
    void regionsResolveAllKnownCodesAndRejectUnknownCodes() {
        for (RegionEnum region : RegionEnum.values()) {
            assertSame(region, RegionEnum.getFromCode(region.getCode()));
            assertFalse(region.getDescription().isBlank());
        }
        assertNull(RegionEnum.getFromCode("unknown"));
        assertNull(RegionEnum.getFromCode(null));
        assertEquals("/errors", ErrorClassEnum.GENERIC.getType());
    }

    @Test
    void responseAndExceptionConstructorsPreserveDiagnosticInformation() {
        LogTraceInfoDTO trace = new LogTraceInfoDTO("span", "trace");
        ErrorResponseDTO error = new ErrorResponseDTO(trace, "type", "title", "detail", 400, "instance");
        assertEquals("trace", error.getTraceID());
        assertEquals("span", error.getSpanID());
        assertEquals("detail", error.getDetail());
        assertEquals("trace", new ErrorResponseDTO(trace).getTraceID());
        assertNull(new ResponseDTO().getTraceID());
        assertNull(new LogControllerResDTO().getEntity());
        Exception cause = new Exception("cause");
        assertEquals("message", new BusinessException("message").getMessage());
        assertSame(cause, new BusinessException("message", cause).getCause());
        assertSame(cause, new BusinessException(cause).getCause());
        assertSame(error, new ValidationException(error).getError());
        assertSame(cause, new ValidationException("message", cause).getCause());
        assertSame(cause, new ValidationException(cause).getCause());
    }
}
