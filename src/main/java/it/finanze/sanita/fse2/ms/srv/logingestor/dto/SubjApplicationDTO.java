package it.finanze.sanita.fse2.ms.srv.logingestor.dto;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjApplicationDTO {
    
    @Field("subject_application_id")
    private String subject_application_id;

    @Field("subject_application_vendor")
    private String subject_application_vendor;

    @Field("subject_application_version")
    private String subject_application_version;

}
