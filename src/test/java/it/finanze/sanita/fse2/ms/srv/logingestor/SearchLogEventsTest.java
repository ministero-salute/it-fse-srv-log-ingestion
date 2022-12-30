/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;
import it.finanze.sanita.fse2.ms.srv.logingestor.controller.ISearchLogEventsCTL;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(Constants.Profile.TEST)
class SearchLogEventsTest extends AbstractTest {

	@Autowired
	ServletWebServerApplicationContext webServerAppCtxt;

	@Autowired
	MockMvc mvc; 

	@Autowired
	private ISearchLogEventsCTL searchLogEventsCTL;

    @Test
	void livenessCheckCtlTest() throws Exception {
		mvc.perform(get("http://localhost:" + webServerAppCtxt.getWebServer().getPort() + webServerAppCtxt.getServletContext().getContextPath() + "/status").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(
	            status().is2xxSuccessful()
	        );
	} 
	
	String getBaseUrl() {
		return "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + webServerAppCtxt.getServletContext().getContextPath() + "/v1";
	}

	@Test
	void getAllLogs() throws Exception {
		createControlLogMockEvents();

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.get(getBaseUrl() + "/search-events-log")
				.queryParam("region", "110")
				.queryParam("docType", "Lettera di dimissione ospedaliera")
				.param("startDate", "2022-01-01T00:00:00.000+00:00")
				.param("endDate", "2022-01-01T00:00:00.000+00:00");

		mvc.perform(builder.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is2xxSuccessful())
				.andExpect(result -> {
					MockHttpServletResponse response = result.getResponse();
					assertAll(
							() -> assertNotNull(response.getContentAsByteArray()),
							() -> assertTrue(StringUtils.isNotEmpty(new String(response.getContentAsByteArray())))
					);
				});
	}

	@Test
	void getLogsValidationFailureTest() throws Exception {
		createControlLogMockEvents();

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.get(getBaseUrl() + "/search-events-log")
				.queryParam("region", "110")
				.queryParam("docType", "Lettera di dimissione ospedaliera")
				.param("startDate", "2022-01-02T00:00:00.000+00:00")
				.param("endDate", "2022-01-01T00:00:00.000+00:00");

		mvc.perform(builder.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest());
	}
}
