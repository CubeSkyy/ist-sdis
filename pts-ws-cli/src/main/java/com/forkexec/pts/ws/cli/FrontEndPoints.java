
package com.forkexec.pts.ws.cli;

import java.util.concurrent.ConcurrentHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.forkexec.pts.ws.TupleView;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;


import com.forkexec.pts.ws.*;
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
    private int Q;

    private ConcurrentHashMap<String, Integer> tags = new ConcurrentHashMap<>();

    // Singleton
    private FrontEndPoints() {
        properties = new Properties();
        try {
            properties.load(FrontEndPoints.class.getResourceAsStream(PROP_FILE));
        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {}", PROP_FILE);
            System.out.println(msg);
        }
        uddiURL = properties.getProperty("uddi.url");
        pointsWsName = properties.getProperty("pts.ws.name");
    }

    private static class SingletonHolder {

        private static final FrontEndPoints INSTANCE = new FrontEndPoints();
    }

    public static synchronized FrontEndPoints getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private int getQ(){ return Q;}

    // replicas read/write ----------------------------------------------

    public TupleView pointsBalance(String userEmail) {
        List<Future<TupleView> > futures = new ArrayList<>();
        List<PointsClient> lpc = getPointsServers();

        for(PointsClient pc : lpc){
            futures.add(pc.pointsBalanceAsync(userEmail));
        }

        int done;
        TupleView max = new TupleView();
        max.setTag(-1);
        while(true){
            done = 0;
            for ( Future<TupleView> fu : futures)
                if( fu.isDone()){
                    done++;
                    try {
                        TupleView tvTemp = fu.get();
                    } catch (ExecutionException | InterruptedException ee){
                        ee.printStackTrace();
                    }
                    if (tvTemp.getTag() > max.getTag()){
                        max = tvTemp;
                    }
                }
            if(done >= getQ()) break;
            else try {
                    Thread.sleep(420);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        return max;
    }

    public int write(String userEmail, int value) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        //TupleView t = point

        return 1;
    }

    // remote invocation methods ----------------------------------------------
    public void activateUser(String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {

        int nExceptions = 0;
        int nExceptions2 = 0;

        for (PointsClient pc : getPointsServers()) {
            try {
                pc.activateUser(userEmail);
            } catch (EmailAlreadyExistsFault_Exception e) {
                nExceptions++;
                if (nExceptions == this.Q)
                    throwEmailAlreadyExistsFault("O e-mail escolhido já está em uso. Por favor escolha outro!");

            } catch (InvalidEmailFault_Exception e) {
                nExceptions2++;
                if (nExceptions2 == this.Q)
                    throwInvalidEmailFault("O e-mail é inválido!");
            }
        }
    }


    public void addPoints(String userEmail, int pointsToAdd) throws InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {

        if (pointsToAdd <= 0) {
            throwInvalidPointsFault("Os pontos não podem ser negativos!");
        }

        int var = pointsBalance(userEmail).getValue();
        write(userEmail, var + pointsToAdd);
    }

    public void spendPoints(String userEmail, int pointsToSpend) throws InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceException {

        if (pointsToSpend <= 0) {
            throwInvalidPointsFault("Os pontos não podem ser negativos!");
        }

        int newBalance = pointsBalance(userEmail).getValue() - pointsToSpend;
        if (newBalance < 0 ){
            throw new NotEnoughBalanceException("Não tem saldo suficiente.");
        }

        write(userEmail, newBalance);
    }

    // helper functions -----------------------------------------------------

    private List<PointsClient> getPointsServers() {
        List<PointsClient> pClients = new ArrayList<>();
        try {
            UDDINaming uddi = new UDDINaming(uddiURL);
            Collection<UDDIRecord> uddiRecords = uddi.listRecords(pointsWsName + "%");

            for (UDDIRecord record : uddiRecords)
                pClients.add(new PointsClient(record.getUrl()));

        } catch (UDDINamingException e) {
            System.out.println("No points servers found.");
        }catch (PointsClientException e){
            throw new RuntimeException(e.getMessage());
        }

        return pClients;
    }


    public void setQ(int numberPS) {
        this.Q = (int) Math.floor(numberPS / 2) + 1;
    }

    // control operations -----------------------------------------------------

    public String ctrlPing(String inputMessage) {

        String str = "";

        //try{
        for (PointsClient pc : getPointsServers()) {
            str = str + pc.ctrlPing(inputMessage);
        }
        //} catch (PointsClientException e){
        //  throw new RuntimeException(e.getMessage());
        //}

        return str;
    }

    public void ctrlClear() {
        //try {
        for (PointsClient pc : getPointsServers()) {
            pc.ctrlClear();
        }
        //  } catch (PointsClientException e){
        //    throw new RuntimeException(e.getMessage());
        //  }
    }

    public void ctrlInit(int startPoints) throws BadInitFault_Exception {

        try {
            for (PointsClient pc : getPointsServers()) {
                pc.ctrlInit(startPoints);
            }
        } catch (BadInitFault_Exception e) {
            throwBadInit("Os pontos não podem ser negativos!");
        }
    }

    // Exception helpers -----------------------------------------------------

    /**
     * Helper to throw a new BadInit exception.
     */
    private void throwBadInit(final String message) throws BadInitFault_Exception {
        final BadInitFault faultInfo = new BadInitFault();
        faultInfo.setMessage(message);
        throw new BadInitFault_Exception(message, faultInfo);
    }

    private void throwInvalidEmailFault(final String message) throws InvalidEmailFault_Exception {
        final InvalidEmailFault faultInfo = new InvalidEmailFault();
        faultInfo.setMessage(message);
        throw new InvalidEmailFault_Exception(message, faultInfo);
    }

    private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
        final InvalidPointsFault faultInfo = new InvalidPointsFault();
        faultInfo.setMessage(message);
        throw new InvalidPointsFault_Exception(message, faultInfo);
    }


    private void throwEmailAlreadyExistsFault(final String message) throws EmailAlreadyExistsFault_Exception {
        final EmailAlreadyExistsFault faultInfo = new EmailAlreadyExistsFault();
        faultInfo.setMessage(message);
        throw new EmailAlreadyExistsFault_Exception(message, faultInfo);
    }

}
