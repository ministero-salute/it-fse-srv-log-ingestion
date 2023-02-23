package it.finanze.sanita.fse2.ms.srv.logingestor.config.health;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.kafka.KafkaPropertiesCFG;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

	@Autowired
    private KafkaPropertiesCFG kafkaCFG;

    @Override
    public Health health() {
    	Properties configProperties = new Properties();
    	configProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCFG.getProducerBootstrapServers());
        try(AdminClient adminClient = AdminClient.create(configProperties)) {
            adminClient.listTopics().listings().get();
            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}