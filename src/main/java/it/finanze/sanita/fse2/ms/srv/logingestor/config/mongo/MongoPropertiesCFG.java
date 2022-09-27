package it.finanze.sanita.fse2.ms.srv.logingestor.config.mongo;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Component
@EqualsAndHashCode(callSuper = false)
public class MongoPropertiesCFG implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4258085875471414048L;
	
	@Value("${data.mongodb.uri}")
	private String uri;

}
