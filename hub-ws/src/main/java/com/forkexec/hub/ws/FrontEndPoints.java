
package com.forkexec.hub.ws;

import java.util.concurrent.ConcurrentHashMap;
import com.forkexec.hub.domain.InvalidEmailException;
import com.forkexec.hub.domain.NotEnoughBalanceException;
import com.forkexec.hub.domain.BadInitException;
import com.forkexec.hub.domain.InvalidPointsException;
import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.NotEnoughBalanceFault_Exception;
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
    private int quantity;

    private ConcurrentHashMap<String, Integer> tags = new ConcurrentHashMap<>();

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

    public int write(String userEmail, int value){
        return 0;
    }

    // remote invocation methods ----------------------------------------------
    public void activateUser(String userEmail) throws InvalidEmailException {

        int nServers = 0;
        int nServers2 = 0;
        int nServers3 = 0;

        for (PointsClient pc : getPointsServers()) {
          try {
            pc.activateUser(userEmail);
          } catch (EmailAlreadyExistsFault_Exception e){
            nServers++;
            if(nServers == this.quantity){
              throw new InvalidEmailException("O e-mail escolhido já está em uso. Por favor escolha outro!");
            }
          } catch (InvalidEmailFault_Exception e){
            nServers2++;
            if(nServers2 == this.quantity){
              throw new InvalidEmailException("O e-mail é inválido!");
            }
          } //catch (PointsClientException e) {
            //nServers3++;
            //if(nServers3 == this.quantity){
            //  throw new RuntimeException(e.getMessage());
            //}
        //  }
        }
    }


    public void addPoints(String userEmail, int pointsToAdd) throws InvalidPointsException, InvalidPointsException {
        //catch InvalidPointsFault_Exception
        //catch InvalidEmailFault_Exception
        //catch PointsClientException

        if(pointsToAdd <= 0) {
          throw new InvalidPointsException("Os pontos não podem ser negativos!");
        }
      //  try {
          int var = pointsBalance(userEmail);
          write(userEmail, var + pointsToAdd);
      //  } catch (InvalidEmailFault_Exception e) {
      //    throw new InvalidEmailException("E-mail invalido!");
      //  } catch (InvalidPointsFault_Exception e) {
      //    throw new InvalidPointsException("Os pontos não podem ser negativos!");
      //  } catch (PointsClientException e) {
      //    throw new RuntimeException(e.getMessage());
      //  }
    }

    public void spendPoints(String userEmail, int pointsToSpend) throws NotEnoughBalanceException, InvalidPointsException, InvalidEmailException {
        //catch InvalidEmailFault_Exception
        //catch InvalidPointsFault_Exception
        //catch NotEnoughBalanceException
        if (pointsToSpend <= 0) {
          throw new InvalidPointsException("Os pontos não podem ser negativos!");
        }
        //try{
          int var = pointsBalance(userEmail);
          write(userEmail, var - pointsToSpend);
        //} catch (InvalidEmailFault_Exception e) {
        //  throw new InvalidEmailException("E-mail invalido!");
        //} catch (InvalidPointsFault_Exception e) {
        //  throw new InvalidPointsException("Os pontos não podem ser negativos!");
        //}
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


    public void setQuantity(int numberPS) {
      quantity = (int) Math.floor(numberPS / 2) + 1;
    }

    // control operations -----------------------------------------------------

    public String ctrlPing(String inputMessage) {

      String str = "";

      //try{
        for(PointsClient pc: getPointsServers()) {
          str = str + pc.ctrlPing(inputMessage);
        }
      //} catch (PointsClientException e){
      //  throw new RuntimeException(e.getMessage());
      //}

      return str;
    }

    public void ctrlClear() {
      //try {
        for(PointsClient pc: getPointsServers()) {
          pc.ctrlClear();
        }
    //  } catch (PointsClientException e){
    //    throw new RuntimeException(e.getMessage());
    //  }
    }

    public void ctrlInit(int startPoints) throws BadInitException {

        try {
          for(PointsClient pc: getPointsServers()) {
            pc.ctrlInit(startPoints);
          }
        } catch (BadInitFault_Exception e) {
          throw new BadInitException("Os pontos não podem ser negativos!");
        } //catch (PointsClientException e) {
          //throw new RuntimeException(e.getMessage());
        //}
    }

}
