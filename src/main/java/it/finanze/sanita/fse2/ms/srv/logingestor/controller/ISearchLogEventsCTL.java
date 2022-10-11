package it.finanze.sanita.fse2.ms.srv.logingestor.controller;


import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.LogControllerResDTO;

@RequestMapping(path = "/v1")
public interface ISearchLogEventsCTL {
	
	@GetMapping(value = "/search-events-log")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LogControllerResDTO.class)))
	@Operation(summary = "Ricerca log", description = "Ricerca dei record delle loggate in base a data, regione e tipo documento")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Ricerca eseguita correttamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LogControllerResDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	LogControllerResDTO getLogEvents(@RequestParam(value="region", required = false) String region, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, 
			 @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @RequestParam(value="docType", required = false) String docType);
}
