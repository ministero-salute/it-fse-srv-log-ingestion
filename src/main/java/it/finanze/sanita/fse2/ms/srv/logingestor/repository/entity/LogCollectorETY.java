package it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "#{@logCollectorBean}")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogCollectorETY {
	
	@Id
	private String id;
	
	@Field(name = "document")
	private org.bson.Document doc;

}
