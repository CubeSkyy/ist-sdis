package com.forkexec.hub.domain;


import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;

import java.util.ArrayList;
import java.util.Collection;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Hub
 * <p>
 * A restaurants hub server.
 */
public class Hub {
    private final String uddiURL = "http://t02:noRpzUdr@uddi.sd.rnl.tecnico.ulisboa.pt:9090";

    // Singleton -------------------------------------------------------------

	private Map<String, HubFoodOrder> cartMap = new ConcurrentHashMap<>();

	/** Private constructor prevents instantiation from other classes. */
	private Hub() {
		// Initialization of default values
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {

		private static final Hub INSTANCE = new Hub();
	}

    public static synchronized Hub getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void activateAccount(String userId) throws InvalidEmailException {
        try {
            PointsClient client = getPointsClient();
            client.activateUser(userId);
        } catch (EmailAlreadyExistsFault_Exception e) {
            throw new InvalidEmailException("O email e invalido!");
        } catch (InvalidEmailFault_Exception e) {
            throw new InvalidEmailException("O email e invalido!");
        } catch (PointsClientException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public PointsClient getPointsClient() throws PointsClientException {
	    PointsClient client = null;
	    client = new PointsClient("http://t02:noRpzUdr@uddi.sd.rnl.tecnico.ulisboa.pt:9090","T02_Points1");
	    return client;
	}

	public RestaurantClient getRestaurantClient() throws RestaurantClientException {
		RestaurantClient client = null;
		client = new RestaurantClient("http://t02:noRpzUdr@uddi.sd.rnl.tecnico.ulisboa.pt:9090","T02_Points1");
		return client;
	}

	public HubFoodOrder getFoodCart(String userId) /*throws InvalidEmailException*/{
		/*
		* TODO: Verificar a eligibilidade do user!
		 */
		return cartMap.get(userId);
	}

	public HubFoodOrder createFoodCart(String userId){
		/*
		 * TODO: Verificar a eligibilidade do user!
		 */
		return cartMap.put(userId,new HubFoodOrder());
	}
	public void clearFoodCart(String userId){
		cartMap.remove(userId);
	}

	public int getPoints(HubFoodOrderItem hfoi){
		
		return 0;
	}
}

