package com.forkexec.pts.ws;

import javax.jws.WebService;
import com.forkexec.pts.domain.*;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;


import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.pts.ws.PointsPortType", wsdlLocation = "PointsService.wsdl", name = "PointsWebService", portName = "PointsPort", targetNamespace = "http://ws.pts.forkexec.com/", serviceName = "PointsService")
public class PointsPortImpl {


    /**
     * The Endpoint manager controls the Web Service instance during its whole
     * lifecycle.
     */
    private final PointsEndpointManager endpointManager;

    /**
     * Constructor receives a reference to the endpoint manager.
     */
    public PointsPortImpl(final PointsEndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    // Main operations -------------------------------------------------------

    public void activateUser(final String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
        if (userEmail == null
                || userEmail.trim().length() == 0)
            throwInvalidEmailFault("Email invalido!");

        Points p = Points.getInstance();

        try {
            p.addUser(userEmail);
        } catch (EmailAlreadyExistsException eaef) {
            throwEmailAlreadyExistsFault("Email invalido!" + eaef.getMessage());
        } catch (InvalidEmailException e) {
            throwInvalidEmailFault("Email invalido!");
        }
    }

        public TupleView pointsBalance(final String userEmail) throws InvalidEmailFault_Exception {
            if (userEmail == null
                    || userEmail.trim().length() == 0)
            throwInvalidEmailFault("Email invalido!");

            Points p = Points.getInstance();
            System.out.print("Read " + userEmail + " ");

            Map<String, Tuple> mapOfUsers = p.getUsers();
            Tuple value = mapOfUsers.get(userEmail);
            if(value == null){
                try{
                    value = p.addUser(userEmail);
                } catch (InvalidEmailException | EmailAlreadyExistsException e) {
                    throwInvalidEmailFault("Email invalido!");
                }
            }
            System.out.print(value.getValue());
            System.out.println();
            return buildTupleView(value);
        }

    public Integer write(String userEmail, int amount, int tag) throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        if (userEmail == null
                || userEmail.trim().length() == 0)
            throwInvalidEmailFault("Email invalido!");

        if(amount < 0)
            throwInvalidPointsFault("Quantidade de pontos a ser adicionada invalida!");

        Points p = Points.getInstance();

        Map<String, Tuple> mapOfUsers = p.getUsers();
        Tuple value = mapOfUsers.get(userEmail);
        if (value == null) {
            try {
                value = p.addUser(userEmail);
            } catch (EmailAlreadyExistsException | InvalidEmailException e) {
                throwInvalidEmailFault("Email Invalido!");
            }
        }

        if (tag > value.getTag()){
            p.getUsers().put(userEmail, new Tuple(tag,amount));
        }

        return 1;
    }

    // Control operations ----------------------------------------------------
    public String ctrlPing(String inputMessage) {
        // If no input is received, return a default name.
        if (inputMessage == null || inputMessage.trim().length() == 0)
            inputMessage = "friend";

        // If the park does not have a name, return a default.
        String wsName = endpointManager.getWsName();
        if (wsName == null || wsName.trim().length() == 0)
            wsName = "Park";

        // Build a string with a message to return.
        final StringBuilder builder = new StringBuilder();
        builder.append("Hello ").append(inputMessage);
        builder.append(" from ").append(wsName);
        return builder.toString();
    }

    /**
     * Return all variables to default values.
     */
    public void ctrlClear() {
        Points p = Points.getInstance();
        p.resetState();
    }

    /**
     * Set variables with specific values.
     */
    public void ctrlInit(final int startPoints) throws BadInitFault_Exception {
        if (startPoints < 0)
            throwBadInit("Cannot have negative points");
        Points p = Points.getInstance();
        p.setInitialBalance(startPoints);
    }


    // View helpers ----------------------------------------------------------
    private TupleView buildTupleView (Tuple t){
        TupleView tv = new TupleView();

        tv.setTag(t.getTag());
        tv.setValue(t.getValue());

        return tv;
    }


    // Exception helpers -----------------------------------------------------

    /**
     * Helper to throw a new BadInit exception.
     */
    private void throwBadInit(final String message) throws BadInitFault_Exception {
        final BadInitFault faultInfo = new BadInitFault();
        faultInfo.message = message;
        throw new BadInitFault_Exception(message, faultInfo);
    }

    private void throwInvalidEmailFault(final String message) throws InvalidEmailFault_Exception {
        final InvalidEmailFault faultInfo = new InvalidEmailFault();
        faultInfo.message = message;
        throw new InvalidEmailFault_Exception(message, faultInfo);
    }

    private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
        final InvalidPointsFault faultInfo = new InvalidPointsFault();
        faultInfo.message = message;
        throw new InvalidPointsFault_Exception(message, faultInfo);
    }

//    private void throwNotEnoughBalanceFault(final String message) throws NotEnoughBalanceFault_Exception {
//        final NotEnoughBalanceFault faultInfo = new NotEnoughBalanceFault();
//        faultInfo.message = message;
//        throw new NotEnoughBalanceFault_Exception(message, faultInfo);
//    }

    private void throwEmailAlreadyExistsFault(final String message) throws EmailAlreadyExistsFault_Exception {
        final EmailAlreadyExistsFault faultInfo = new EmailAlreadyExistsFault();
        faultInfo.message = message;
        throw new EmailAlreadyExistsFault_Exception(message, faultInfo);
    }
}
