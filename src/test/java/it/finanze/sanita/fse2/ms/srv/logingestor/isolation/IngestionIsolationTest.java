/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.srv.logingestor.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.opentelemetry.api.trace.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.handler.ControllerExceptionHandler;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.impl.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.exceptions.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.impl.LogEventsRepo;
import it.finanze.sanita.fse2.ms.srv.logingestor.service.impl.*;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({LogCollectorCTL.class, SearchLogEventsCTL.class, LivenessCheckCTL.class,
        ControllerExceptionHandler.class, LogEventsSRV.class, KafkaSRV.class, LogEventsRepo.class})
class IngestionIsolationTest {
    @Autowired LogCollectorCTL collector;
    @Autowired SearchLogEventsCTL search;
    @Autowired LivenessCheckCTL liveness;
    @Autowired ControllerExceptionHandler handler;
    @Autowired LogEventsSRV service;
    @Autowired KafkaSRV kafka;
    @Autowired LogEventsRepo repository;
    @MockitoBean MongoTemplate mongo;
    @MockitoBean Tracer tracer;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        SpanBuilder builder = mock(SpanBuilder.class);
        when(tracer.spanBuilder(anyString())).thenReturn(builder);
        when(builder.startSpan()).thenReturn(Span.wrap(SpanContext.create(
                "0123456789abcdef0123456789abcdef", "0123456789abcdef",
                TraceFlags.getSampled(), TraceState.getDefault())));
        mvc = MockMvcBuilders.standaloneSetup(collector, search, liveness).setControllerAdvice(handler).build();
    }

    @Test
    void httpIngestionDecodesAndPersistsControlLog() throws Exception {
        String json = """
                {"log_type":"control-structured-log","message":"accepted","operation":"CREATE",
                 "op_timestamp_start":"01-10-2026 12:34:56.123","op_timestamp_end":"01-10-2026 12:34:57.123",
                 "op_issuer":"prefix#120123456789#suffix",
                 "op_locality":"x&2.16.840.1.113883.2.9.4.1.3&ISO^^^^120123456789",
                 "op_application_id":"app","op_application_vendor":"vendor","op_application_version":"1"}
                """;
        mvc.perform(post("/v1/log").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.spanID").value("0123456789abcdef"))
                .andExpect(jsonPath("$.traceID").value("0123456789abcdef0123456789abcdef"));
        ArgumentCaptor<LogCollectorBase> saved = ArgumentCaptor.forClass(LogCollectorBase.class);
        verify(mongo).save(saved.capture());
        LogCollectorBase entity = saved.getValue();
        assertInstanceOf(LogCollectorControlETY.class, entity);
        assertEquals("accepted", entity.getMessage());
        assertEquals("120", entity.getIssuer().getRegion());
        assertEquals("123", entity.getIssuer().getAsl());
        assertEquals("456789", entity.getLocality().getStructure());
        assertEquals("app", entity.getOpSubjApplication().getSubject_application_id());
        assertEquals("vendor", entity.getOpSubjApplication().getSubject_application_vendor());
        assertEquals("1", entity.getOpSubjApplication().getSubject_application_version());
        assertEquals(1000, entity.getTimestampEnd().getTime() - entity.getTimestampStart().getTime());
    }

    @Test
    void dataPreparationAndKafkaUseRealPersistencePipeline() throws Exception {
        String json = "{\"log_type\":\"kpi-structured-log\",\"message\":\"kpi\"}";
        mvc.perform(post("/v1/log/data-prep/2/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
        service.srvListenerTest(json, 0, 1);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("logs", 0, 1, "key", json);
        kafka.listenerGtwTopic(record, new MessageHeaders(Map.of()));
        kafka.listenerEdsTopic(record, new MessageHeaders(Map.of()));
        ArgumentCaptor<LogCollectorBase> saved = ArgumentCaptor.forClass(LogCollectorBase.class);
        verify(mongo, times(4)).save(saved.capture());
        assertTrue(saved.getAllValues().stream().allMatch(e -> e instanceof LogCollectorKpiETY
                && e.getTimestampStart() == null && e.getIssuer() == null && e.getOpSubjApplication() == null));
    }

    @Test
    void invalidJsonAndStorageFailureBecomeProblemResponses() throws Exception {
        mvc.perform(post("/v1/log").contentType(MediaType.APPLICATION_JSON).content("not json"))
                .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Fornire un json corretto"));
        mvc.perform(post("/v1/log/data-prep/2/1").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(mongo);
        doThrow(new IllegalStateException("offline")).when(mongo).save(any(LogCollectorBase.class));
        mvc.perform(post("/v1/log").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.status").value(500));
        assertThrows(BusinessException.class, () -> repository.saveLogEvent("{\"op_timestamp_start\":\"bad date\"}"));
        assertThrows(BusinessException.class, () -> repository.saveLogEvent("invalid"));
    }

    @Test
    void kafkaFailuresPreserveNestedCausesForBothListeners() {
        IllegalStateException failure = new IllegalStateException("offline", new IllegalArgumentException("root"));
        doThrow(failure).when(mongo).save(any(LogCollectorBase.class));
        ConsumerRecord<String, String> record = new ConsumerRecord<>("logs", 0, 1, "key", "{}");
        BusinessException gtw = assertThrows(BusinessException.class, () -> kafka.listenerGtwTopic(record, null));
        assertSame(failure, gtw.getCause().getCause());
        BusinessException eds = assertThrows(BusinessException.class, () -> kafka.listenerEdsTopic(record, null));
        assertSame(failure, eds.getCause().getCause());
    }

    @Test
    void partialSubjectApplicationAndEmptyIssuerAreHandledWithoutExtraFields() {
        repository.saveLogEvent("{\"op_issuer\":\"\",\"op_locality\":\"\",\"op_application_vendor\":\"vendor\"}");
        ArgumentCaptor<LogCollectorBase> saved = ArgumentCaptor.forClass(LogCollectorBase.class);
        verify(mongo).save(saved.capture());
        assertNull(saved.getValue().getIssuer());
        assertNull(saved.getValue().getLocality());
        assertNull(saved.getValue().getOpSubjApplication().getSubject_application_id());
        assertEquals("vendor", saved.getValue().getOpSubjApplication().getSubject_application_vendor());
        assertNull(saved.getValue().getOpSubjApplication().getSubject_application_version());
    }

    @Test
    void searchesBuildBoundedSortedQueriesAndReturnResults() {
        LogCollectorControlETY entity = new LogCollectorControlETY();
        entity.setMessage("found");
        when(mongo.find(any(Query.class), eq(LogCollectorControlETY.class))).thenReturn(List.of(entity));
        Date start = new Date(100000000), end = new Date(200000000);
        assertSame(entity, search.getLogEvents("120", start, end, "REF").getEntity().getFirst());
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).find(query.capture(), eq(LogCollectorControlETY.class));
        Query actual = query.getValue();
        assertEquals(100, actual.getLimit());
        assertEquals(new Document("op_timestamp_start", -1), actual.getSortObject());
        assertEquals(new Document("_id", 0), actual.getFieldsObject());
        assertEquals("120", actual.getQueryObject().get("op_issuer.region"));
        assertEquals("REF", actual.getQueryObject().get("op_document_type"));
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime((Date) actual.getQueryObject().get("op_timestamp_start", Document.class).get("$gte"));
        assertEquals(0, calendar.get(java.util.Calendar.HOUR_OF_DAY));
        calendar.setTime((Date) actual.getQueryObject().get("op_timestamp_end", Document.class).get("$lte"));
        assertEquals(23, calendar.get(java.util.Calendar.HOUR_OF_DAY));
        assertEquals(59, calendar.get(java.util.Calendar.MINUTE));
        assertSame(entity, search.getLogEventsByWorkflowId("workflow").getEntity().getFirst());
        verify(mongo).find(argThat(q -> "workflow".equals(q.getQueryObject().get("workflow_instance_id"))), eq(LogCollectorControlETY.class));
        repository.getLogEvents("", start, end, null);
        verify(mongo).find(argThat(q -> q.getQueryObject().containsKey("op_timestamp_start")
                && !q.getQueryObject().containsKey("op_issuer.region")
                && !q.getQueryObject().containsKey("op_document_type")), eq(LogCollectorControlETY.class));
    }

    @Test
    void searchRejectsInvalidDatesAndWrapsDatabaseErrors() {
        Date date = new Date();
        assertThrows(ValidationException.class, () -> search.getLogEvents(null, null, date, null));
        assertThrows(ValidationException.class, () -> search.getLogEvents(null, date, null, null));
        assertThrows(ValidationException.class, () -> search.getLogEvents(null, new Date(2), new Date(1), null));
        when(mongo.find(any(Query.class), eq(LogCollectorControlETY.class))).thenThrow(new IllegalStateException("offline"));
        BusinessException dates = assertThrows(BusinessException.class, () -> service.getLogEvents(null, date, date, null));
        assertInstanceOf(BusinessException.class, dates.getCause());
        BusinessException workflow = assertThrows(BusinessException.class, () -> service.getLogEventsByWorkflowId("id"));
        assertEquals("offline", workflow.getCause().getMessage());
    }

    @Test
    void endpointsRemainUsableWhenTracerHasNoBuilder() throws Exception {
        when(tracer.spanBuilder(anyString())).thenReturn(null);
        mvc.perform(post("/v1/log").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.traceID").isEmpty());
        mvc.perform(post("/v1/log").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.traceID").isEmpty());
        mvc.perform(get("/status")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
}
