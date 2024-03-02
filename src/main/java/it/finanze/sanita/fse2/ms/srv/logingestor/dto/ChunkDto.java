package it.finanze.sanita.fse2.ms.srv.logingestor.dto;

import java.util.List;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.srv.logingestor.repository.entity.LogCollectorKpiETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkDto {

	List<? extends LogCollectorBase> records;

	List<LogCollectorControlETY> controllRecord;

	List<LogCollectorKpiETY> kpiRecord;

	String logType;

}
