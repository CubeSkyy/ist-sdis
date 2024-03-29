package com.forkexec.hub.domain;


import com.forkexec.cc.ws.cli.CCClient;
import com.forkexec.cc.ws.cli.CCClientException;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.cli.FrontEndPoints;

import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.rst.ws.*;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Hub
 * <p>
 * A restaurants hub server.
 */
public class Hub {

    protected static Properties properties;
    private static final String PROP_FILE = "/pom.properties";

    private final AtomicInteger orderNumber = new AtomicInteger(0);
    private final String uddiURL;
    private final String ccWsName = "CC";
    private FrontEndPoints fe = FrontEndPoints.getInstance();


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
            fe.activateUser(userId);
        } catch (EmailAlreadyExistsFault_Exception e) {
            throw new InvalidEmailException(e.getMessage());
        } catch (InvalidEmailFault_Exception e) {
            throw new InvalidEmailException(e.getMessage());
        }

    }


    public Integer getCorrespondingPoints(Integer euros) {
        return traductionT.get(euros);
    }

    public void loadAccount(String uId, int euros, String ccnumber) throws InvalidEmailException, InvalidPointsException, InvalidChargeException, InvalidCCException {
        try {
            CCClient ccClient = new CCClient(uddiURL, ccWsName);
            if (!ccClient.validateNumber(ccnumber))
                throw new InvalidCCException("Numero de CC invalido!");
        } catch (CCClientException e) {
            throw new RuntimeException();
        }

        Integer points = traductionT.get(euros);
        if (points != null)
            try {
                System.out.println(points);
                fe.addPoints(uId, points);
            } catch (InvalidPointsFault_Exception e) {
                throw new InvalidPointsException(e.getMessage());
            } catch (EmailAlreadyExistsFault_Exception e) {
                throw new InvalidEmailException(e.getMessage());
            } catch (InvalidEmailFault_Exception e) {
                throw new InvalidEmailException(e.getMessage());
            }
        else
            throw new InvalidChargeException("Por favor carregue com 10, 20, 30 ou 50 euros!");


    }

    public int accountBalance(String uID) throws InvalidEmailException {
        try{
            System.out.println("hub.accountBalance");
            return fe.pointsBalance(uID);
        }catch (InvalidEmailFault_Exception e){
            throw new InvalidEmailException(e.getMessage());
        }
    }

    public void ctrlInitUserPoints(int startPts) throws BadInitException {
        try {
            fe.ctrlInit(startPts);
        } catch (com.forkexec.pts.ws.BadInitFault_Exception e) {
            throw new BadInitException(e.getMessage());
        }
    }

    /**
     * Verifica a eligibilidade do user por ir buscar o saldo dele ao points e deveolve o carrinho dele
     *
     * @param userId
     * @return HubFoodOrder
     * @throws InvalidEmailException
     */
    public HubFoodOrder getFoodCart(String userId) throws InvalidEmailException {
        if (accountBalance(userId) >= 0) {
            return cartMap.get(userId);
        }
        return null;
    }

    /**
     * Verifica a eligibilidade do user ao ir buscar o saldo desse e devolve a HubOrder dessa pessoa
     *
     * @param userId
     * @return
     * @throws InvalidEmailException
     */
    public HubFoodOrder createFoodCart(String userId) throws InvalidEmailException {

        if (accountBalance(userId) >= 0) {
            HubFoodOrder hfo = new HubFoodOrder();
            HubFoodOrderId hofi = new HubFoodOrderId();
            hofi.setId("HUBORDER" + orderNumber.getAndIncrement());
            hfo.setFoodOrderId(hofi);
            cartMap.put(userId, hfo);
            return hfo;
        }

        return null;
    }

    public void clearFoodCart(String userId) throws InvalidEmailException {
        if (accountBalance(userId) >= 0) {
            cartMap.remove(userId);
        }
    }


    public int getPoints(HubFoodOrderItem hfoi) {
        return getCorrespondingPoints(hfoi.getFoodPrice());
    }

    /**
     * Percorre a lista de restaurantList e faz orderMenu para cada uma das entradas
     * E faz spendPoints do total de pontos do menu ao user.
     *
     * @param userId
     * @param restaurantList
     * @param totalPoints
     * @throws BadOrderException
     */
    public void confirmOrder(String userId, Map<String, List<HubFoodOrderItem>> restaurantList, int totalPoints) throws BadOrderException, PointsClientException, NotEnoughBalanceException, InvalidPointsException, InvalidEmailException {
        for (Map.Entry<String, List<HubFoodOrderItem>> entry : restaurantList.entrySet()) {
            String wsUrl = entry.getKey();
            List<HubFoodOrderItem> hubFoodInitList = entry.getValue();
            try {
                RestaurantClient client = new RestaurantClient(uddiURL, wsUrl);
                for (HubFoodOrderItem hfoi : hubFoodInitList) {
                    MenuId mi = new MenuId();
                    mi.setId(hfoi.getFoodId().getMenuId());
                    client.orderMenu(mi, hfoi.getFoodQuantity());
                }
            } catch (RestaurantClientException e) {
                throw new RuntimeException();
            } catch (com.forkexec.rst.ws.BadQuantityFault_Exception
                    | com.forkexec.rst.ws.BadMenuIdFault_Exception
                    | com.forkexec.rst.ws.InsufficientQuantityFault_Exception e) {
                throw new BadOrderException(e.getMessage());
            }
        }
        try{
            fe.spendPoints(userId, totalPoints);

        } catch (com.forkexec.pts.ws.cli.NotEnoughBalanceException e){
            throw new InvalidPointsException(e.getMessage());
        } catch (InvalidPointsFault_Exception e) {
            throw new InvalidPointsException(e.getMessage());
        } catch (EmailAlreadyExistsFault_Exception e) {
            throw new InvalidEmailException(e.getMessage());
        } catch (InvalidEmailFault_Exception e) {
            throw new InvalidEmailException(e.getMessage());
        }

    }


    public List<HubFood> searchHungry(Collection<UDDIRecord> restaurants, String description) throws BadTextException {
        List<HubFood> foodList = searchMenus(restaurants, description);

        foodList.sort(HubFood.preparationTimeComparator);
        return foodList;
    }

    public List<HubFood> searchDeal(Collection<UDDIRecord> restaurants, String description) throws BadTextException {
        List<HubFood> foodList = searchMenus(restaurants, description);


        foodList.sort(HubFood.priceComparator);
        return foodList;
    }

    public void ctrlClear(Collection<UDDIRecord> restaurant_list) {
        cartMap.clear();
        try {
            fe.ctrlClear();
            for (UDDIRecord uddiRecord : restaurant_list) {
                RestaurantClient restaurantClient = new RestaurantClient(uddiRecord.getUrl());
                restaurantClient.ctrlClear();
            }
        } catch (RestaurantClientException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public HubFood getFood(HubFoodId hubFoodId, String wsUrl) throws InvalidFoodIdException {
        String restaurantId = hubFoodId.getRestaurantId();
        String menuId = hubFoodId.getMenuId();
        if (menuId == null)
            throw new InvalidFoodIdException("Id de menu inválido!");
        Menu menu;
        try {
            RestaurantClient client = new RestaurantClient(wsUrl);
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
                List<MenuInit> menuInitList = new LinkedList<>();
                for (HubFoodInit hfi : hubFoodInitList) {
                    menuInitList.add(buildMenuInit(hfi));
                }

                client.ctrlInit(menuInitList);
            } catch (RestaurantClientException e) {
                throw new RuntimeException();
            } catch (com.forkexec.rst.ws.BadInitFault_Exception e) {
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
        HubFoodId hubFoodId = new HubFoodId(restaurantId, menu.getId().getId());
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


    private MenuId buildMenuId(HubFoodId hfi) {
        MenuId menuId = new MenuId();
        menuId.setId(hfi.getMenuId());
        return menuId;
    }

    private Menu buildMenu(HubFood hf) {
        Menu m = new Menu();
        m.setId(buildMenuId(hf.getId()));
        m.setEntree(hf.getEntree());
        m.setPlate(hf.getPlate());
        m.setDessert(hf.getDessert());
        m.setPrice(hf.getPrice());
        m.setPreparationTime(hf.getPreparationTime());

        return m;
    }

    private MenuInit buildMenuInit(HubFoodInit hfi) {
        MenuInit mi = new MenuInit();
        mi.setMenu(buildMenu(hfi.getFood()));
        mi.setQuantity(hfi.getQuantity());

        return mi;
    }

}
