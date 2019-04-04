package com.forkexec.hub.domain;

import javax.jws.WebService;

import com.forkexec.hub.ws.HubEndpointManager;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import com.forkexec.pts.ws.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;



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

	private Map<String, List<HubFoodOrder>> cartMap = new ConcurrentHashMap<>();

    private final String uddiURL = "http://t02:noRpzUdr@uddi.sd.rnl.tecnico.ulisboa.pt:9090";

	private static final Map<Integer, Integer> traductionT = new ConcurrentHashMap<Integer, Integer>();
    static {
        traductionT.put(10, 1000);
        traductionT.put(20, 2100);
        traductionT.put(30, 3300);
        traductionT.put(50, 5500);
    }

    // Singleton -------------------------------------------------------------

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

 	public Map<Integer, Integer> getTraductionTable(){
 		return traductionT;
 	}


	public Integer getCorrespondingPoints(Integer euros){
		return traductionT.get(euros);
	}

	public void loadAccount(String uId, int euros, String ccnumber) throws InvalidEmailException, InvalidPointsException, InvalidChargeException{
		try{
			Integer test = 0;
			PointsClient client = getPointsClient();
			//if(!client.validateNumber(ccnumber)) {
			//	throw new InvalidCCException("Numero de CC invalido!");
			//}

			for(Integer i: traductionT.values()){
				if(euros == i){
					client.addPoints(uId, i);
					test = 1;
				}
			}

			if (test == 0) {
				throw new InvalidChargeException("Por favor carregue com 10, 20, 30 ou 50 euros!");
			}

		} catch (PointsClientException e) {
	        throw new RuntimeException();
	    } catch (InvalidEmailFault_Exception e) {
	    	throw new InvalidEmailException("O email nao e valido!");
	    } catch (InvalidPointsFault_Exception e) {
	    	throw new InvalidPointsException("Os pontos tem de ser positivos!");
	    }
	
	}

	public int accountBalance(String uID) throws InvalidEmailException {
		try {
			PointsClient client = getPointsClient();
			return client.pointsBalance(uID);
		} catch (InvalidEmailFault_Exception e) {
			throw new InvalidEmailException("O email nao e valido!");
		} catch (PointsClientException e) {
	        throw new RuntimeException();
	    }
	}

	public void ctrlInitUserPoints(int startPts) throws BadInitException {
		try {
			PointsClient client = getPointsClient();
			client.ctrlInit(startPts);
		} catch (BadInitFault_Exception e) {
			throw new BadInitException("Nao pode ter pontos negativos!");
		} catch (PointsClientException e) {
	        throw new RuntimeException();
	    }
	}
	

	public List<HubFoodOrder > getListFoods (String userId) throws NoCartForUser {
		List<HubFoodOrder > listFood;
		listFood = cartMap.get(userId);
		if (listFood == null) {
			throw new NoCartForUser("Cart para esse user não existe!");
		}
		return listFood;
	}

	public HubFoodOrder getFood(String userId, HubFoodId id) throws NoCartForUser, NoSuchHubFoodId {
		List<HubFoodOrder > listFood = getListFoods(userId);
		HubFoodOrder hid =  listFood.stream().filter(i-> i.equals(id)).findAny().orElse(null);
		if(hid == null) throw new NoSuchHubFoodId("Food ID invalido!");
		return hid;
	}

	public void addFoodId(String userId,HubFoodOrder id)  {
		List<HubFoodOrder> lhid = cartMap.get(userId);
		if(lhid == null){
			// TODO: Verificacao se o user tem AccountBalance, se tiver é porque pode se criar um cart para ele
			//cartMap.put(userId, Stream.concat(Stream.of(id),new ArrayList<HubFoodId>().stream()).toArray());
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

