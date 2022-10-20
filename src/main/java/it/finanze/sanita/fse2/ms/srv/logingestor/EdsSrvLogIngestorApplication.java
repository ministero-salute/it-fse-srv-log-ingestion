/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class })
@SpringBootApplication
public class EdsSrvLogIngestorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdsSrvLogIngestorApplication.class, args);
	}

}
