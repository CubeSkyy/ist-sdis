package com.forkexec.pts.ws;

import javax.jws.WebService;
import com.forkexec.pts.domain.*;
import java.util.AbstractMap.SimpleEntry;
/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.pts.ws.PointsPortType", wsdlLocation = "PointsService.wsdl", name = "PointsWebService", portName = "PointsPort", targetNamespace = "http://ws.pts.forkexec.com/", serviceName = "PointsService")
public class PointsPortImpl implements PointsPortType {


    /**
     * The Endpoint manager controls the Web Service instance during its whole
     * lifecycle.
     */
    private final PointsEndpointManager endpointManager;

    /** Constructor receives a reference to the endpoint manager. */
    public PointsPortImpl(final PointsEndpointManager endpointManager) {
	this.endpointManager = endpointManager;
    }

    // Main operations -------------------------------------------------------

    @Override
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

    /*
    @Override
    public Integer[] pointsBalance(final String userEmail) throws InvalidEmailFault_Exception {
        if (userEmail == null
                || userEmail.trim().length() == 0)
        throwInvalidEmailFault("Email invalido!");

        try{
            Points p = Points.getInstance();
            SimpleEntry<Integer,Integer> s = p.getPoints(userEmail);
            Integer[] pair = new Integer[2];
            pair[0] = s.getKey();
            pair[1] = s.getValue();
            return pair;
        } catch (InvalidEmailException iee) {
            throwInvalidEmailFault("Email invalido!" + iee.getMessage());
        }
        return null;
    }*/

    public int pointsBalance(String UserEmail){
      return 0;
    }

    public int write (String s, int t){
      return 0;
    }
/*
    @Override
    public int write(final String userEmail, int ammount, int tag) throws InvalidEmailFault_Exception, InvalidPointsFault_Exception,EmailAlreadyExistsException, InvalidEmailException {
        if (userEmail == null
                || userEmail.trim().length() == 0)
            throwInvalidEmailFault("Email invalido!");

        if(ammount < 0)
            throwInvalidPointsFault("Quantidade de pontos a ser adicionada invalida!");

        Points p = Points.getInstance();
        if (!p.checkUserExists(userEmail)){
            p.addUser(userEmail);
        }

        p.setUserBalance(userEmail, ammount, tag);

        return 1;
    }*/


//    @Override
//    public int addPoints(final String userEmail, final int pointsToAdd)
//	    throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
//        if (userEmail == null
//                || userEmail.trim().length() == 0)
//            throwInvalidEmailFault("Email invalido!");
//
//        if(pointsToAdd <= 0)
//            throwInvalidPointsFault("Quantidade de pontos a ser adicionada invalida!");
//        Points p = Points.getInstance();
//        try {
//            return p.addPoints(userEmail, pointsToAdd);
//        } catch (InvalidEmailException ief) {
//            throwInvalidEmailFault("Email invalido!" + ief.getMessage());
//        }
//        return -1;
//    }
//
//    @Override
//    public int spendPoints(final String userEmail, final int pointsToSpend)
//	    throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
//        if (userEmail == null
//                || userEmail.trim().length() == 0)
//            throwInvalidEmailFault("Email invalido!");
//
//        if(pointsToSpend <= 0)
//            throwInvalidPointsFault("Quantidade de pontos a ser gasto invalida!");
//        Points p = Points.getInstance();
//
//        try {
//            return p.subtractPoints(userEmail, pointsToSpend);
//        } catch (InvalidEmailException ief) {
//            throwInvalidEmailFault("Email invalido!" + ief.getMessage());
//        } catch (NotEnoughBalanceException nebf) {
//            throwNotEnoughBalanceFault("Não tem saldo suficiente!" + nebf.getMessage());
//        }
//        return -1;
//    }

    // Control operations ----------------------------------------------------
    @Override
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

    /** Return all variables to default values. */
    @Override
    public void ctrlClear() {
        Points p = Points.getInstance();
        p.resetState();
    }

    /** Set variables with specific values. */
    @Override
    public void ctrlInit(final int startPoints) throws BadInitFault_Exception {
        if (startPoints < 0)
            throwBadInit("Cannot have negative points");
        Points p = Points.getInstance();
        p.setInitialBalance(startPoints);
    }

    // Exception helpers -----------------------------------------------------

    /** Helper to throw a new BadInit exception. */
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
