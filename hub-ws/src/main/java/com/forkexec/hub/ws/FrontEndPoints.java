package com.forkexec.hub.ws;

import com.forkexec.hub.domain.InvalidEmailException;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class FrontEndPoints {

    protected static Properties properties;
    private static final String PROP_FILE = "/pom.properties";


    private final String uddiURL;
    private final String pointsWsName;

    // Singleton
    private FrontEndPoints(){
        properties = new Properties();
        try {
            properties.load(FrontEndPoints.class.getResourceAsStream(PROP_FILE));
        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {}", PROP_FILE);
            System.out.println(msg);
        }
        uddiURL =  properties.getProperty("uddi.url");
        pointsWsName = properties.getProperty("pts.ws.name");
    }

    private static class SingletonHolder {

        private static final FrontEndPoints INSTANCE = new FrontEndPoints();
    }

    public static synchronized FrontEndPoints getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // replicas read/write ----------------------------------------------

    //Mudar para retornar Int,Tag
    public int read(String userEmail){
        return 0;
    }

    public int write(String userEmail, int value){
        return 0;
    }

    // remote invocation methods ----------------------------------------------
    public void activateUser(String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailException {
        //catch EmailAlreadyExistsFault_Exception
        //catch InvalidEmailFault_Exception
        //catch PointsClientException
    }

    public int pointsBalance(String userEmail) {
        return 0;
    }

    public int addPoints(String userEmail, int pointsToAdd) {
        return 0;
    }

    public int spendPoints(String userEmail, int pointsToSpend) {
        return 0;
    }

    // helper functions -----------------------------------------------------

    private List<PointsClient> getPointsServers(){
        List<PointsClient> pClients = new ArrayList<>();
        try {
            UDDINaming uddi = new UDDINaming(uddiURL);
            Collection<UDDIRecord> uddiRecords = uddi.listRecords(pointsWsName + "%");

            for (UDDIRecord record : uddiRecords)
                pClients.add(new PointsClient(record.getUrl()));

        } catch (PointsClientException | UDDINamingException e) {
            System.out.println("No points servers found.");
        }

        return pClients;
    }

    // control operations -----------------------------------------------------

    public String ctrlPing(String inputMessage) {
        return "";
    }

    public void ctrlClear() {
    }

    public void ctrlInit(int startPoints) {

    }


}
