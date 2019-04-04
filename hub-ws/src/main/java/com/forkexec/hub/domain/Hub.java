package com.forkexec.hub.domain;

import javax.jws.WebService;

import com.forkexec.cc.ws.cli.CCClient;
import com.forkexec.cc.ws.cli.CCClientException;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.rst.ws.BadTextFault_Exception;
import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;

import com.forkexec.pts.ws.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;


/**
 * Hub
 * <p>
 * A restaurants hub server.
 */
public class Hub {

    private Map<String, List<HubFoodOrder>> cartMap = new ConcurrentHashMap<>();

    private final String uddiURL = "http://t02:noRpzUdr@uddi.sd.rnl.tecnico.ulisboa.pt:9090";
    private final String pointsWsName = "T02_Points1";
    private final String ccWsName = "CC";


    private static final Map<Integer, Integer> traductionT = new ConcurrentHashMap<Integer, Integer>();

    static {
        traductionT.put(10, 1000);
        traductionT.put(20, 2100);
        traductionT.put(30, 3300);
        traductionT.put(50, 5500);
    }

    // Singleton -------------------------------------------------------------

    /**
     * Private constructor prevents instantiation from other classes.
     */
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
        client = new PointsClient(uddiURL, pointsWsName);
        return client;
    }

    public RestaurantClient getRestaurantClient() throws RestaurantClientException {
        RestaurantClient client = null;
        client = new RestaurantClient(uddiURL, "T02_Points1");
        return client;
    }

    public Map<Integer, Integer> getTraductionTable() {
        return traductionT;
    }


    public Integer getCorrespondingPoints(Integer euros) {
        return traductionT.get(euros);
    }

    public void loadAccount(String uId, int euros, String ccnumber) throws InvalidEmailException, InvalidPointsException, InvalidChargeException, InvalidCCException {
        try {
//            Integer test = 0;
            CCClient ccClient = new CCClient(uddiURL, ccWsName);
            if (!ccClient.validateNumber(ccnumber)) {
                throw new InvalidCCException("Numero de CC invalido!");
            }
            PointsClient client = getPointsClient();
//            for (Integer i : traductionT.values()) {
//                if (euros == i) {
//                    client.addPoints(uId, i);
//                    test = 1;
//                }
//            }

            Integer points = traductionT.get(euros);

            if (points != null)
                client.addPoints(uId, points);
            else
                throw new InvalidChargeException("Por favor carregue com 10, 20, 30 ou 50 euros!");

//            if (test == 0) {
//                throw new InvalidChargeException("Por favor carregue com 10, 20, 30 ou 50 euros!");
//            }

        } catch (PointsClientException | CCClientException e) {
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


    public List<HubFoodOrder> getListFoods(String userId) throws NoCartForUser {
        List<HubFoodOrder> listFood;
        listFood = cartMap.get(userId);
        if (listFood == null) {
            throw new NoCartForUser("Cart para esse user não existe!");
        }
        return listFood;
    }

    public HubFoodOrder getFood(String userId, HubFoodId id) throws NoCartForUser, NoSuchHubFoodId {
        List<HubFoodOrder> listFood = getListFoods(userId);
        HubFoodOrder hid = listFood.stream().filter(i -> i.equals(id)).findAny().orElse(null);
        if (hid == null) throw new NoSuchHubFoodId("Food ID invalido!");
        return hid;
    }

    public void addFoodId(String userId, HubFoodOrder id) {
        List<HubFoodOrder> lhid = cartMap.get(userId);
        if (lhid == null) {
            // TODO: Verificacao se o user tem AccountBalance, se tiver é porque pode se criar um cart para ele
            //cartMap.put(userId, Stream.concat(Stream.of(id),new ArrayList<HubFoodId>().stream()).toArray());
        }
        //	try {
        //		getListFoods(userId).add(id);
        //	} catch (NoCartForUser ncfu) {

        //		}
        //	}
    }

    public void addCartToUser(String userId) {

    }

    public List<HubFood> searchHungry(Collection<UDDIRecord> restaurants, String description) throws BadTextException{
        List<HubFood> foodList = searchMenus(restaurants, description);

        foodList.sort(HubFood.priceComparator);
        return foodList;
    }

    public List<HubFood> searchDeal(Collection<UDDIRecord> restaurants, String description) throws BadTextException{
        List<HubFood> foodList = searchMenus(restaurants, description);

        foodList.sort(HubFood.preparationTimeComparator);
        return foodList;
    }

    //Given a description and a list of Restaurant WS url's searches their menus and builds a unsorted HubFoodList with results from search
    private List<HubFood> searchMenus(Collection<UDDIRecord> restaurants, String description) throws BadTextException {
        List<HubFood> foodList = new ArrayList<>();
        try {
            for (UDDIRecord ws : restaurants) {
                RestaurantClient client = new RestaurantClient(ws.getUrl());
                foodList.addAll(buildHubList(client.searchMenus(description), ws.getOrgName()));
            }
        } catch (RestaurantClientException e) {
            throw new RuntimeException();
        }catch (BadTextFault_Exception e){
            throw new BadTextException("O texto de procura é invalido!");
        }
        return foodList;
    }

    //Builds HubFood fom a menu and a given restaurant
    private HubFood buildHubFood(Menu menu, String restaurantId) {
        HubFood hubFood = new HubFood();
        HubFoodId hubFoodId = new HubFoodId();
        hubFoodId.setMenuId(menu.getId().getId());
        hubFoodId.setRestaurantId(restaurantId);
        hubFood.setId(hubFoodId);
        hubFood.setEntree(menu.getEntree());
        hubFood.setPlate(menu.getPlate());
        hubFood.setDessert(menu.getDessert());
        hubFood.setPrice(menu.getPrice());
        hubFood.setPreparationTime(menu.getPreparationTime());
        return hubFood;
    }
    //Builds HubFoodList for one restaurant from a munuList
    private List<HubFood> buildHubList(List<Menu> menuList, String restaurantId){
        List<HubFood> hubFoodList = new ArrayList<>();

        for(Menu menu : menuList){
            hubFoodList.add(buildHubFood(menu, restaurantId));
        }

        return hubFoodList;
    }

}

