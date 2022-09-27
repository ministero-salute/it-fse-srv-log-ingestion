package it.finanze.sanita.fse2.ms.srv.logingestor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 *  Microservices URL.
 */
@Configuration
@Getter
public class MicroservicesURLCFG {

    /** 
     *  Log Ingestor host.
     */
	@Value("${ms.srv-log-ingestor}")
	private String logIngestorHost;

}
