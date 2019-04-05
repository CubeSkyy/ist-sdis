package com.forkexec.hub.domain;

import javax.jws.WebService;

import com.forkexec.cc.ws.cli.CCClient;
import com.forkexec.cc.ws.cli.CCClientException;
import com.forkexec.hub.ws.FoodId;
import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.rst.ws.*;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;

import com.forkexec.pts.ws.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Hub
 * <p>
 * A restaurants hub server.
 */
public class Hub {

    protected static Properties properties;
    private static final String PROP_FILE = "/pom.properties";


    private final String uddiURL;
    private final String pointsWsName;
    private final String ccWsName = "CC";


    private static final Map<Integer, Integer> traductionT = new ConcurrentHashMap<Integer, Integer>();

    static {
        traductionT.put(10, 1000);
        traductionT.put(20, 2100);
        traductionT.put(30, 3300);
        traductionT.put(50, 5500);
    }
    private Map<String, HubFoodOrder> cartMap = new ConcurrentHashMap<>();
    // Singleton -------------------------------------------------------------

    /**
     * Private constructor prevents instantiation from other classes.
     */
    private Hub() {
        properties = new Properties();
        try {
            properties.load(Hub.class.getResourceAsStream(PROP_FILE));
        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {}", PROP_FILE);
            System.out.println(msg);
        }
        uddiURL = properties.getProperty("uddi.url");
        pointsWsName = properties.getProperty("pts.ws.name");
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
            throw new RuntimeException(e.getMessage());
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


    public List<HubFood> searchHungry(Collection<UDDIRecord> restaurants, String description) throws BadTextException {
        List<HubFood> foodList = searchMenus(restaurants, description);

        foodList.sort(HubFood.priceComparator);
        return foodList;
    }

    public List<HubFood> searchDeal(Collection<UDDIRecord> restaurants, String description) throws BadTextException {
        List<HubFood> foodList = searchMenus(restaurants, description);

        foodList.sort(HubFood.preparationTimeComparator);
        return foodList;
    }

    public HubFood getFood(HubFoodId hubFoodId) throws InvalidFoodIdException {
        String restaurantId = hubFoodId.getRestaurantId();
        String menuId = hubFoodId.getMenuId();
        if (menuId == null)
            throw new InvalidFoodIdException("Id de menu inválido!");
        Menu menu;
        try {
            RestaurantClient client = new RestaurantClient(restaurantId);
            MenuId mid = new MenuId();
            mid.setId(menuId);
            menu = client.getMenu(mid);
        } catch (RestaurantClientException e) {
            throw new RuntimeException();
        } catch (BadMenuIdFault_Exception e) {
            throw new InvalidFoodIdException("Não existe menu com ID " + menuId + "!");
        }


        return buildHubFood(menu, restaurantId);
    }


    public void ctrlInitFood(Map<String, LinkedList<HubFoodInit>> restaurant_init) throws BadInitException {

        for (Map.Entry<String, LinkedList<HubFoodInit>> entry : restaurant_init.entrySet()) {
            String wsUrl = entry.getKey();
            LinkedList<HubFoodInit> hubFoodInitList = entry.getValue();
            try {
                RestaurantClient client = new RestaurantClient(wsUrl);
                LinkedList<MenuInit> menuInitList = new LinkedList<>();

                for(HubFoodInit hfi: hubFoodInitList){
                    menuInitList.add(buildMenuInit(hfi));
                }
                client.ctrlInit(menuInitList);
            } catch (RestaurantClientException e) {
                throw new RuntimeException();
            }catch (com.forkexec.rst.ws.BadInitFault_Exception e){
                throw new BadInitException(e.getMessage());
            }

        }
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
        } catch (BadTextFault_Exception e) {
            throw new BadTextException("O texto de procura é invalido!");
        }
        return foodList;
    }

    //Builds HubFood fom a menu and a given restaurant
    private HubFood buildHubFood(Menu menu, String restaurantId) {
        HubFood hubFood = new HubFood();
        HubFoodId hubFoodId = new HubFoodId(menu.getId().getId(),restaurantId);
        hubFood.setId(hubFoodId);
        hubFood.setEntree(menu.getEntree());
        hubFood.setPlate(menu.getPlate());
        hubFood.setDessert(menu.getDessert());
        hubFood.setPrice(menu.getPrice());
        hubFood.setPreparationTime(menu.getPreparationTime());
        return hubFood;
    }


    //Builds HubFoodList for one restaurant from a munuList
    private List<HubFood> buildHubList(List<Menu> menuList, String restaurantId) {
        List<HubFood> hubFoodList = new ArrayList<>();

        for (Menu menu : menuList) {
            hubFoodList.add(buildHubFood(menu, restaurantId));
        }

        return hubFoodList;
    }

    private MenuId buildMenuId(HubFoodId hfi){
        MenuId menuId = new MenuId();
        menuId.setId(hfi.getMenuId());
        return menuId;
    }

    private Menu buildMenu(HubFood hf){
        Menu m = new Menu();
        m.setId(buildMenuId(hf.getId()));
        m.setEntree(hf.getEntree());
        m.setPlate(hf.getPlate());
        m.setDessert(hf.getDessert());
        m.setPrice(hf.getPrice());
        m.setPreparationTime(hf.getPreparationTime());

        return m;
    }

    private MenuInit buildMenuInit(HubFoodInit hfi){
        MenuInit mi = new MenuInit();
        mi.setMenu(buildMenu(hfi.getFood()));
        mi.setQuantity(hfi.getQuantity());

        return mi;
    }


}

