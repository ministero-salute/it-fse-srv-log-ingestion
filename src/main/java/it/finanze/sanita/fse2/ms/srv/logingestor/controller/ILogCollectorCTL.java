package it.finanze.sanita.fse2.ms.srv.logingestor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.ChunkDto;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.EsitoDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.srv.logingestor.dto.response.ResponseDTO;

@RequestMapping(path = "/v1/log")
@Tag(name = "Log Collector Controller")
public interface ILogCollectorCTL {
	
	@PostMapping(value = "")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
	@Operation(summary = "Inserimento log strutturato", description = "Inserimento log strutturato")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Inserimento log strutturato", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	@ResponseStatus(HttpStatus.CREATED)
	ResponseDTO createLogEvents(@RequestBody String logJson);
	
	@PostMapping(value = "/data-prep/{numDocumenti}/{numThread}")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
	@Operation(summary = "Inserimento log strutturato", description = "Inserimento log strutturato")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Inserimento log strutturato", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	@ResponseStatus(HttpStatus.CREATED)
	ResponseDTO createLogEventsDataPrep(@PathVariable Integer numDocumenti,@PathVariable Integer numThread,@RequestBody String logJson);

	@PostMapping(value = "/process-chunk")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)))
	@Operation(summary = "Inserimento dei log in chunk in ingresso", description = "")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Inserimento dei log in chunk in ingresso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EsitoDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	@ResponseStatus(HttpStatus.CREATED)
	EsitoDTO createLogsFromChunk(@RequestBody ChunkDto chunk);

}
