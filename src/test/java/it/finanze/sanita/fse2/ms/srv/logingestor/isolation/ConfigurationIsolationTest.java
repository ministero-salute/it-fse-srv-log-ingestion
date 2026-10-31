/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.srv.logingestor.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.*;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import it.finanze.sanita.fse2.ms.srv.logingestor.config.OpenTelemetryConfig;
import it.finanze.sanita.fse2.ms.srv.logingestor.config.health.KafkaHealthIndicator;
import it.finanze.sanita.fse2.ms.srv.logingestor.config.kafka.*;
import it.finanze.sanita.fse2.ms.srv.logingestor.config.mongo.MongoDatabaseCFG;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.CollectionNaming;
import it.finanze.sanita.fse2.ms.srv.logingestor.utility.ProfileUtility;

@Tag("ISOLATION")
class ConfigurationIsolationTest {
    @Test
    void telemetryRegistersSdkAndCreatesApplicationTracerWithoutExporters() {
        GlobalOpenTelemetry.resetForTest();
        AutoConfiguredOpenTelemetrySdk sdk = AutoConfiguredOpenTelemetrySdk.builder()
                .addPropertiesSupplier(() -> Map.of("otel.traces.exporter", "none",
                        "otel.metrics.exporter", "none", "otel.logs.exporter", "none"))
                .build();
        try (var context = context(OpenTelemetryConfig.class)) {
            context.registerBean(AutoConfiguredOpenTelemetrySdk.class, () -> sdk);
            context.refresh();
            Tracer tracer = context.getBean(Tracer.class);
            assertNotNull(tracer);
            var span = tracer.spanBuilder("isolation").startSpan();
            try {
                assertTrue(span.getSpanContext().isValid());
            } finally {
                span.end();
            }
        } finally {
            sdk.getOpenTelemetrySdk().close();
            GlobalOpenTelemetry.resetForTest();
        }
    }

    // Only explicitly registered production beans: no Boot auto-configuration or network clients.
    private AnnotationConfigApplicationContext context(Class<?>... types) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("ISOLATION", Map.ofEntries(
                Map.entry("kafka.bootstrap-servers", "localhost:9092"),
                Map.entry("kafka.consumer.bootstrap-servers", "localhost:9092"),
                Map.entry("kafka.consumer.client-id", "consumer"), Map.entry("kafka.consumer.group-id", "group"),
                Map.entry("kafka.consumer.key-deserializer", "org.apache.kafka.common.serialization.StringDeserializer"),
                Map.entry("kafka.consumer.value-deserializer", "org.apache.kafka.common.serialization.StringDeserializer"),
                Map.entry("kafka.consumer.isolation.level", "read_committed"), Map.entry("kafka.consumer.auto-commit", "false"),
                Map.entry("kafka.consumer.auto-offset-reset", "earliest"),
                Map.entry("kafka.consumer.dead-letter-exc", "{'java.lang.IllegalArgumentException'}"),
                Map.entry("kafka.properties.security.protocol", "SASL_SSL"),
                Map.entry("kafka.properties.sasl.mechanism", "SCRAM-SHA-256"),
                Map.entry("kafka.properties.sasl.jaas.config", "login"),
                Map.entry("kafka.properties.ssl.truststore.location", "truststore.jks"),
                Map.entry("kafka.properties.ssl.truststore.password", "secret"), Map.entry("kafka.enablessl", "false"),
                Map.entry("kafka.producer.client-id", "producer"), Map.entry("kafka.producer.retries", "5"),
                Map.entry("kafka.producer.key-serializer", "org.apache.kafka.common.serialization.StringSerializer"),
                Map.entry("kafka.producer.value-serializer", "org.apache.kafka.common.serialization.StringSerializer"),
                Map.entry("kafka.producer.transactional.id", "transaction"), Map.entry("kafka.producer.enable.idempotence", "true"),
                Map.entry("kafka.producer.ack", "all"), Map.entry("kafka.log.base-topic", "logs"),
                Map.entry("kafka.log.base-eds-topic", "eds"), Map.entry("kafka.srv-log-ingestor.deadletter.topic", "logs-dlt"),
                Map.entry("kafka.srv-log-ingestor-eds.deadletter.topic", "eds-dlt"))));
        context.getEnvironment().setActiveProfiles("ISOLATION");
        context.register(types);
        return context;
    }

    @Test
    void profileDetectionAndCollectionNamingFollowFirstActiveProfile() {
        ProfileUtility uninitialized = new ProfileUtility();
        assertFalse(uninitialized.isTestProfile());
        assertFalse(uninitialized.isDevOrDockerProfile());
        try (var context = context(ProfileUtility.class, CollectionNaming.class)) {
            context.refresh();
            ProfileUtility profile = context.getBean(ProfileUtility.class);
            CollectionNaming naming = new CollectionNaming();
            context.getAutowireCapableBeanFactory().autowireBean(naming);
            for (String name : List.of("TEST", "dev", "docker", "prod")) {
                context.getEnvironment().setActiveProfiles(name);
                assertEquals(name.equals("TEST"), profile.isTestProfile());
                assertEquals(name.equals("dev") || name.equals("docker"), profile.isDevOrDockerProfile());
                assertEquals((name.equals("TEST") ? "test_" : "") + "log_collector", naming.getLogCollectorControlCollection());
                assertEquals((name.equals("TEST") ? "test_" : "") + "log_collector_kpi", naming.getLogCollectorKpiCollection());
            }
            context.getEnvironment().setActiveProfiles();
            assertFalse(profile.isTestProfile());
            assertFalse(profile.isDevOrDockerProfile());
        }
    }

    @Test
    void consumerConfigurationSupportsSslAndInvalidDeadLetterClasses() {
        try (var context = context(ProfileUtility.class, KafkaTopicCFG.class, KafkaConsumerPropertiesCFG.class, KafkaConsumerCFG.class)) {
            KafkaTemplate<Object, Object> dead = mock(KafkaTemplate.class);
            when(dead.isTransactional()).thenReturn(false);
            context.registerBean("notxkafkadeadtemplate", KafkaTemplate.class, () -> dead);
            context.registerBean("notxkafkatemplate", KafkaTemplate.class, () -> mock(KafkaTemplate.class));
            context.refresh();
            KafkaConsumerCFG config = new KafkaConsumerCFG();
            context.getAutowireCapableBeanFactory().autowireBean(config);
            assertEquals("group", config.consumerConfigs().get("group.id"));
            assertFalse(config.consumerConfigs().containsKey("security.protocol"));
            KafkaConsumerPropertiesCFG properties = context.getBean(KafkaConsumerPropertiesCFG.class);
            properties.setEnableSsl(true);
            Map<String, Object> secure = config.consumerConfigs();
            assertEquals("SASL_SSL", secure.get("security.protocol"));
            assertEquals("secret", secure.get("ssl.truststore.password"));
            assertEquals("truststore.jks", secure.get("ssl.truststore.location"));
            assertEquals("logs", context.getBean(KafkaTopicCFG.class).getLogTopic());
            properties.setDeadLetterExceptions(List.of("does.not.Exist"));
            assertNotNull(config.kafkaListenerIngestorContainerFactory(dead));
            assertNotNull(config.kafkaListenerIngestorEdsContainerFactory(dead));
            context.getEnvironment().setActiveProfiles("test");
            KafkaTopicCFG topics = context.getBean(KafkaTopicCFG.class);
            topics.afterInit();
            assertEquals("test_logs", topics.getLogTopic());
            assertEquals("test_logs-dlt", topics.getLogIngestorDeadLetterTopic());
        }
    }

    @Test
    void producerAndAdminConfigurationKeepSecurityAndTransactionsSeparate() {
        AdminClient admin = mock(AdminClient.class);
        try (var creation = mockStatic(AdminClient.class);
             var context = context(ProfileUtility.class, KafkaPropertiesCFG.class, KafkaProducerPropertiesCFG.class, KafkaProducerCFG.class)) {
            creation.when(() -> AdminClient.create(any(Properties.class))).thenReturn(admin);
            context.refresh();
            KafkaProducerCFG producer = new KafkaProducerCFG();
            context.getAutowireCapableBeanFactory().autowireBean(producer);
            assertTrue(producer.producerConfigs().containsKey("transactional.id"));
            assertEquals("all", producer.producerConfigs().get("acks"));
            assertFalse(producer.producerWithoutTransactionConfigs().containsKey("transactional.id"));
            KafkaPropertiesCFG properties = context.getBean(KafkaPropertiesCFG.class);
            properties.setEnableSSL(true);
            assertEquals("SASL_SSL", producer.producerConfigs().get("security.protocol"));
            assertEquals("secret", producer.producerWithoutTransactionConfigs().get("ssl.truststore.password"));
            assertNotNull(context.getBean("txKafkaTemplate", KafkaTemplate.class));
            assertNotNull(context.getBean("notxKafkaTemplate", KafkaTemplate.class));
            assertNotNull(context.getBean("noTxKafkaDeadTemplate", KafkaTemplate.class));
            for (String profile : List.of("dev", "test", "prod")) {
                context.getEnvironment().setActiveProfiles(profile);
                properties.client();
            }
            ArgumentCaptor<Properties> captured = ArgumentCaptor.forClass(Properties.class);
            creation.verify(() -> AdminClient.create(captured.capture()), times(4));
            assertFalse(captured.getAllValues().get(1).containsKey("security.protocol"));
            assertFalse(captured.getAllValues().get(2).containsKey("security.protocol"));
            assertEquals("SASL_SSL", captured.getAllValues().get(3).get("security.protocol"));
        }
    }

    @Test
    void kafkaHealthReportsUpDownAndRestoresInterrupt() throws Exception {
        AdminClient admin = mock(AdminClient.class);
        ListTopicsResult topics = mock(ListTopicsResult.class);
        KafkaFuture future = mock(KafkaFuture.class);
        when(admin.listTopics()).thenReturn(topics);
        when(topics.listings()).thenReturn(future);
        try (var context = context(KafkaHealthIndicator.class)) {
            context.registerBean(AdminClient.class, () -> admin);
            context.refresh();
            KafkaHealthIndicator health = context.getBean(KafkaHealthIndicator.class);
            assertEquals(Status.UP, health.health().getStatus());
            doThrow(new ExecutionException(new IllegalStateException("offline"))).when(future).get();
            assertEquals(Status.DOWN, health.health().getStatus());
            doThrow(new InterruptedException("interrupted")).when(future).get();
            try {
                assertEquals(Status.DOWN, health.health().getStatus());
                assertTrue(Thread.currentThread().isInterrupted());
            } finally {
                Thread.interrupted();
            }
        }
    }

    @Test
    void mongoConverterOmitsClassDiscriminatorWithoutConnecting() {
        MongoDatabaseFactory factory = mock(MongoDatabaseFactory.class);
        when(factory.getExceptionTranslator()).thenReturn(new org.springframework.data.mongodb.core.MongoExceptionTranslator());
        try (var context = context(MongoDatabaseCFG.class)) {
            context.registerBean(MongoDatabaseFactory.class, () -> factory);
            context.refresh();
            MongoTemplate template = context.getBean(MongoTemplate.class);
            Document document = new Document();
            template.getConverter().write(Map.of("message", "payload"), document);
            assertEquals("payload", document.getString("message"));
            assertFalse(document.containsKey("_class"));
            verify(factory, never()).getMongoDatabase();
        }
    }
}
