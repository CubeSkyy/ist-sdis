
package com.forkexec.pts.ws.cli;

import java.util.concurrent.ConcurrentHashMap;

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
    private int quantity;

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
        setQuantity(3);
    }

    private static class SingletonHolder {

        private static final FrontEndPoints INSTANCE = new FrontEndPoints();
    }

    public static synchronized FrontEndPoints getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // replicas read/write ----------------------------------------------

    public int pointsBalance(String userEmail) {
        //TO DO (read)
        return 0;
    }


    public int write(String userEmail, int value) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        PointsClient p;
        try {
            p = new PointsClient("a");
        } catch (PointsClientException e) {
            throw new RuntimeException(e.getMessage());
        }

        p.write(userEmail, 100, 0);

        return 0;
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
                if (nExceptions == this.quantity)
                    throwEmailAlreadyExistsFault("O e-mail escolhido já está em uso. Por favor escolha outro!");

            } catch (InvalidEmailFault_Exception e) {
                nExceptions2++;
                if (nExceptions2 == this.quantity)
                    throwInvalidEmailFault("O e-mail é inválido!");
            }
        }
    }


    public void addPoints(String userEmail, int pointsToAdd) throws InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {

        if (pointsToAdd <= 0) {
            throwInvalidPointsFault("Os pontos não podem ser negativos!");
        }

        int var = pointsBalance(userEmail);
        write(userEmail, var + pointsToAdd);
    }

    public void spendPoints(String userEmail, int pointsToSpend) throws InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, NotEnoughBalanceException {

        if (pointsToSpend <= 0) {
            throwInvalidPointsFault("Os pontos não podem ser negativos!");
        }

        int newBalance = pointsBalance(userEmail) - pointsToSpend;
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


    public void setQuantity(int numberPS) {
        quantity = (int) Math.floor(numberPS / 2) + 1;
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
