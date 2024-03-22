package it.finanze.sanita.fse2.ms.srv.logingestor.threads;

import it.finanze.sanita.fse2.ms.srv.logingestor.repository.ILogEventsRepo;

public class LogThread extends Thread{

    private ILogEventsRepo logEventsRepo;

    private Integer numDocument;

    private String valueJson;

    /**
     * Constructor
     */
    public LogThread(final ILogEventsRepo inlogEventsRepo, final Integer inNumDocument, final String inValueJson) {
        logEventsRepo = inlogEventsRepo;
        numDocument = inNumDocument;
        valueJson = inValueJson;
    }

    /**
     * Thread body
     */
    @Override
    public void run() {
        logEventsRepo.saveLogsEvent(valueJson, numDocument);
    }
    
}
