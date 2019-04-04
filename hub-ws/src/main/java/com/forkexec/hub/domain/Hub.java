package com.forkexec.hub.domain;

import javax.jws.WebService;

import com.forkexec.hub.ws.HubEndpointManager;
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

	private Map<String, List<HubFoodId>> cartMap = new ConcurrentHashMap<>();

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


	public List<HubFoodId > getListFoods (String userId) throws NoCartForUser {
		List<HubFoodId > listFood;
		listFood = cartMap.get(userId);
		if (listFood == null) {
			throw new NoCartForUser("Cart para esse user não existe!");
		}
		return listFood;
	}

	public HubFoodId getFood(String userId, HubFoodId id) throws NoCartForUser, NoSuchHubFoodId {
		List<HubFoodId > listFood = getListFoods(userId);
		HubFoodId hid =  listFood.stream().filter(i-> i.equals(id)).findAny().orElse(null);
		if(hid == null) throw new NoSuchHubFoodId("Food ID invalido!");
		return hid;
	}

	public void addFoodId(String userId,HubFoodId id)  {
		List<HubFoodId> lhid = cartMap.get(userId);
		if(lhid == null){
			// TODO: Verificacao se o user tem AccountBalance, se tiver é porque pode se criar um cart para ele
			cartMap.put(userId, Stream.concat(Stream.of(id),new ArrayList<HubFoodId>().stream()).toArray());
		}
		//	try {
		//		getListFoods(userId).add(id);
	//	} catch (NoCartForUser ncfu) {

	//		}
	//	}
	}

	public void addCartToUser(String userId){

	}
}

