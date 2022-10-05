package it.finanze.sanita.fse2.ms.srv.logingestor.controller;


import java.util.Date;
import java.util.List;

import org.bson.Document;
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
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.ResponseDTO;

@RequestMapping(path = "/v1")
public interface IControllerTest {
	
	@GetMapping(value = "/get-records")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
	@Operation(summary = "Ricerca records", description = "Ricerca dei record delle loggate in base a data, regione e tipo documento")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Ricerca eseguita correttamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	List<Document> getRecordsByRegion(@RequestParam(value="region", required = false) String region, @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate, 
			 @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate, @RequestParam(value="docType", required = false) String docType);
}
