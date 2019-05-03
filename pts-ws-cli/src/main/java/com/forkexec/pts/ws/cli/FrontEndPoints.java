
package com.forkexec.pts.ws.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.forkexec.pts.ws.*;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
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
    private static final String PROP_FILE = "/FrontEnd.properties";


    private final String uddiURL;
    private final String pointsWsName;

    private final int Q;

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
        int N = Integer.parseInt(properties.getProperty("pts.ws.n"));
        Q = (N / 2) + 1;
    }

    private static class SingletonHolder {

        private static final FrontEndPoints INSTANCE = new FrontEndPoints();
    }

    public static synchronized FrontEndPoints getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private int getQ() {
        return Q;
    }

    // replicas read/write ----------------------------------------------

    public int pointsBalance(String userEmail) throws InvalidEmailFault_Exception {
        List<Future<PointsBalanceResponse>> futures = new ArrayList<>();
        List<PointsClient> lpc = getPointsServers();

        for (PointsClient pc : lpc) {
            futures.add(pc.pointsBalanceAsync(userEmail));
        }

        int done;
        TupleView max = new TupleView();
        max.setTag(-1);
        while (true) {
            done = 0;
            for (Future<PointsBalanceResponse> fu : futures)
                if (fu.isDone()) {
                    done++;
                    TupleView tvTemp;
                    try {
                        tvTemp = fu.get().getReturn();
                        if (tvTemp.getTag() > max.getTag()) {
                            max = tvTemp;
                        }
                    } catch (ExecutionException ee) {
                        Throwable t = ee.getCause();
                        if (t instanceof InvalidEmailFault_Exception)
                            throw (InvalidEmailFault_Exception) t;
                        else ee.printStackTrace();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            if (done >= getQ()) break;
            else try {
                Thread.sleep(420);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return max.getValue();
    }

    public int write(String userEmail, int value) throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        if (userEmail == null
                || userEmail.trim().length() == 0)
            throwInvalidEmailFault("Email invalido!");
        if (value < 0){

        }

        TupleView tv = new TupleView();

        int tag = tags.get(userEmail);
        tv.setTag(tag + 1);
        tags.put(userEmail, tag + 1);
        tv.setValue(value);

        List<Future<WriteResponse>> futures = new ArrayList<>();
        List<PointsClient> lpc = getPointsServers();
        for (PointsClient pc : lpc) {
            futures.add(pc.writeAsync(userEmail, tv.getValue(), tv.getTag()));
        }
        int done;
        while (true) {
            done = 0;
            for (Future<WriteResponse> fu : futures)
                if (fu.isDone()) {
                    done++;
                    try {
                        fu.get();
                    } catch (ExecutionException ee) {
                        Throwable t = ee.getCause();
                        if (t instanceof InvalidEmailFault_Exception)
                            throw (InvalidEmailFault_Exception) t;
                        else if (t instanceof InvalidPointsFault_Exception)
                            throw (InvalidPointsFault_Exception) t;
                        else ee.printStackTrace();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            if (done >= getQ()) break;
            else try {
                Thread.sleep(420);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        tags.put(userEmail, 0);
    }


    public void addPoints(String userEmail, int pointsToAdd) throws InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {

        if (pointsToAdd <= 0) {
            throwInvalidPointsFault("Os pontos não podem ser negativos!");
        }

        int var = pointsBalance(userEmail);
        System.out.println(var);
        System.out.println(pointsToAdd);
        write(userEmail, var + pointsToAdd);
    }

    public void spendPoints(String userEmail, int pointsToSpend) throws InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceException {

        if (pointsToSpend <= 0) {
            throwInvalidPointsFault("Os pontos não podem ser negativos!");
        }

        int newBalance = pointsBalance(userEmail) - pointsToSpend;
        if (newBalance < 0) {
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

            for (UDDIRecord record : uddiRecords) {
                pClients.add(new PointsClient(record.getUrl()));
            }
        } catch (UDDINamingException e) {
            System.out.println("No points servers found.");
        } catch (PointsClientException e) {
            throw new RuntimeException(e.getMessage());
        }

        return pClients;
    }


    // control operations -----------------------------------------------------

    public String ctrlPing(String inputMessage) {
        String str = "";

        for (PointsClient pc : getPointsServers()) {
            str = str + pc.ctrlPing(inputMessage);
        }

        return str;
    }

    public void ctrlClear() {
        for (PointsClient pc : getPointsServers()) {
            pc.ctrlClear();
        }
        tags.clear();
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
