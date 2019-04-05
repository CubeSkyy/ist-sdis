package com.forkexec.hub.ws;

import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.jws.WebService;
import javax.xml.soap.SOAPFault;

import com.forkexec.hub.domain.*;

import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;

import com.sun.xml.ws.fault.ServerSOAPFaultException;

import javax.xml.soap.SOAPFault;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.hub.ws.HubPortType",
        wsdlLocation = "HubService.wsdl",
        name = "HubWebService",
        portName = "HubPort",
        targetNamespace = "http://ws.hub.forkexec.com/",
        serviceName = "HubService"
)
public class HubPortImpl implements HubPortType {

    /**
     * The Endpoint manager controls the Web Service instance during its whole
     * lifecycle.
     */
    private HubEndpointManager endpointManager;
    private Hub h = Hub.getInstance();

    /**
     * Constructor receives a reference to the endpoint manager.
     */
    public HubPortImpl(HubEndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    // Main operations -------------------------------------------------------

    @Override
    public void activateAccount(String userId) throws InvalidUserIdFault_Exception {
        Hub h = Hub.getInstance();
        try {
            h.activateAccount(userId);
        } catch (InvalidEmailException e) {
            throwInvalidUserIdInit("O email e invalido!");
        }
    }

    @Override
    public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
            throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {

        Hub h = Hub.getInstance();
        try {
            h.loadAccount(userId, moneyToAdd, creditCardNumber);
        } catch (InvalidEmailException e) {
            throwInvalidUserIdInit(e.getMessage());
        } catch (InvalidPointsException e) {
            throwInvalidPoints(e.getMessage()); //Esta excepcao nunca vai acontecer, porque os Pontos sao so aqueles que estao no mapa, mas tenho de dar catch na mesma, right?
        } catch (InvalidChargeException e) {
            throwInvalidMoney(e.getMessage());
        } catch (InvalidCCException e) {
            throwInvalidCC(e.getMessage());
        }

    }


    @Override
    public List<Food> searchDeal(String description) throws InvalidTextFault_Exception {
        List<HubFood> hubFoodList = new ArrayList<>();

        try {
            Hub h = Hub.getInstance();
            hubFoodList = h.searchDeal(getRestaurants(), description);
        } catch (BadTextException e) {
            throwBadText(e.getMessage());
        }

        List<Food> foodList = buildFoodList(hubFoodList);
        return foodList;
    }

    @Override
    public List<Food> searchHungry(String description) throws InvalidTextFault_Exception {
        List<HubFood> hubFoodList = new ArrayList<>();

        try {
            Hub h = Hub.getInstance();
            hubFoodList =h.searchHungry(getRestaurants(), description);
        } catch (BadTextException e) {
            throwBadText(e.getMessage());
        }
        List<Food> foodList = buildFoodList(hubFoodList);
        return foodList;
    }



    @Override
    public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
            throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {

        if(userId == null || userId.trim().length()==0)
            throwInvalidUserIdInit("User ID invalido!");

       if(foodId == null || foodId.getMenuId().trim().length()==0)
            throwInvalidFoodIdFault("Food ID invalido!");

        if(foodQuantity <= 0)
            throwInvalidFoodQuantityFault("Quantidade invalida!");

        Hub h = Hub.getInstance();
        Food f = getFood(foodId);
        HubFoodOrder hfo = null;
        try {
             hfo = h.getFoodCart(userId);
        } catch (InvalidEmailException iee) {
            throwInvalidUserIdFault(iee.getMessage());
        }

        if(hfo == null){
            try {
                hfo = h.createFoodCart(userId);
            } catch(InvalidEmailException iee) {
                throwInvalidUserIdFault(iee.getMessage());
            }
        }
        List<HubFoodOrderItem> listItem = hfo.getItems();

        HubFoodOrderItem hfoi = listItem.stream().filter(i -> i.getFoodId().getMenuId().equals(f.getId().getMenuId())).findAny().orElse(null);

        if(hfoi == null)
            listItem.add(new HubFoodOrderItem(buildHubFoodId(f.getId()),foodQuantity,f.getPrice()));
        else
            hfoi.setFoodQuantity(hfoi.getFoodQuantity()+foodQuantity);

    }


    @Override
    public void clearCart(String userId) throws InvalidUserIdFault_Exception {
        if(userId == null || userId.trim().length()==0)
            throwInvalidUserIdInit("User ID invalido!");
        Hub.getInstance().clearFoodCart(userId);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public FoodOrder orderCart(String userId)
            throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {

        if(userId == null || userId.trim().length()==0)
            throwInvalidUserIdInit("User ID invalido!");

        Hub h = Hub.getInstance();

        HubFoodOrder hubOrder = null;
        try {
            hubOrder = h.getFoodCart(userId);
        } catch (InvalidEmailException iee) {
            throwInvalidUserIdFault(iee.getMessage());
        }

        List<HubFoodOrderItem> listItem = hubOrder.getItems();

        if(listItem.size()==0)
            throwEmptyCartFault("Carrinho vazio!");

        int totalPoints = listItem.stream().mapToInt(h::getPoints).sum();
        if(totalPoints < accountBalance(userId))
            throwNotEnoughPointsFault("Não tem saldo suficiente!");

        Map<String, List<HubFoodOrderItem>> restaurantList = new HashMap<>();

        for(HubFoodOrderItem hfoi : listItem){
            String restaurantName = hfoi.getFoodId().getRestaurantId();
            String wsName = getRestaurant(restaurantName);
            List<HubFoodOrderItem> initList = restaurantList.get(wsName);
            if (initList == null) {
                initList = new LinkedList<>();
                restaurantList.put(restaurantName, initList);
            }
            initList.add(hfoi);

        }
        FoodOrderId foi = new FoodOrderId();
        foi.setId(hubOrder.getFoodOrderId().getId());
        FoodOrder fo = new FoodOrder();
        fo.setFoodOrderId(foi);

        List<FoodOrderItem> listFoodOrderItems = fo.getItems();
        listItem.forEach(hubFoodItem -> listFoodOrderItems.add(buildFoodOrderItem(hubFoodItem)));

        try {
            h.confirmOrder(userId, restaurantList, totalPoints);
        } catch (BadOrderException be){
            throwInvalidUserIdInit(be.getMessage());
        } catch (PointsClientException pc) {
            throwNotEnoughPointsFault(pc.getMessage());
        }
        return fo;
    }

    @Override
    public int accountBalance(String userId) throws InvalidUserIdFault_Exception {
        Hub h = Hub.getInstance();
        try {
            return h.accountBalance(userId);
        } catch (InvalidEmailException e) {
            throwInvalidUserIdInit("O email nao e valido!");
        }

        return -1;
    }

    @Override
    public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception {
        if (foodId == null) throwInvalidFoodIdFault("Id de menu inválido!");

        Food food = null;
        try {
            HubFoodId hubFoodId = buildHubFoodId(foodId);
            String wsURL = getRestaurant(foodId.getRestaurantId());
            HubFood hubFood = h.getFood(hubFoodId, wsURL);
            food = buildFood(hubFood);
        } catch (InvalidFoodIdException e) {
            throwInvalidFoodIdFault(e.getMessage());
        }

        return food;
    }


    @SuppressWarnings("Duplicates")
    @Override
    public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception {
       if(userId == null || userId.trim().length()==0)
            throwInvalidUserIdInit("User ID invalido!");

        Hub h = Hub.getInstance();

        HubFoodOrder hubOrder = null;

        try{
            hubOrder= h.getFoodCart(userId);
        } catch (InvalidEmailException iee) {
            throwInvalidUserIdFault(iee.getMessage());
        }

       List<HubFoodOrderItem> listItem = hubOrder.getItems();
        List<FoodOrderItem> listFoodOrderItems = new ArrayList<>();

        listItem.forEach(hubFoodItem -> listFoodOrderItems.add(buildFoodOrderItem(hubFoodItem)));

        return listFoodOrderItems;

    }

    // Control operations ----------------------------------------------------

    /**
     * Diagnostic operation to check if service is running.
     */
    @Override
    public String ctrlPing(String inputMessage) {

        Collection<UDDIRecord> restaurants;
        StringBuilder builder = new StringBuilder();

        try {
            UDDINaming uddiNaming = endpointManager.getUddiNaming();
            restaurants = uddiNaming.listRecords("T02_Restaurant%");
            for (UDDIRecord ws : restaurants) {
                RestaurantClient client = new RestaurantClient(ws.getUrl());

                builder.append(client.ctrlPing("Cliente")).append("\n");
            }
            if (restaurants.size() == 0) {
                return String.format("No restaurants found at %s!", uddiNaming.getUDDIUrl());
            }
        } catch (UDDINamingException e) {
            return e.getMessage();

        } catch (RestaurantClientException e) {
            return e.getMessage();
        }
        return builder.toString();
    }

    /**
     * Return all variables to default values.
     */
    @Override
    public void ctrlClear() {
        Hub h = Hub.getInstance();
        h.ctrlClear(getRestaurants());
    }

    /**
     * Set variables with specific values.
     */
    @Override
    public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {
        Map<String, LinkedList<HubFoodInit>> restaurant_init = new HashMap<>();

        Hub h = Hub.getInstance();
        for (FoodInit fi : initialFoods) {
            String restaurantName = fi.getFood().getId().getRestaurantId();
            String wsUrl = getRestaurant(restaurantName);
            LinkedList<HubFoodInit> initList = restaurant_init.get(wsUrl);
            if (initList == null) {
                initList = new LinkedList<>();
                restaurant_init.put(wsUrl, initList);
            }
            initList.add(buildHubFoodInit(fi));
        }
        try {
            h.ctrlInitFood(restaurant_init);
        } catch (BadInitException e) {
            throwBadInit(e.getMessage());
        }

    }

    @Override
    public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
        try {
            Hub h = Hub.getInstance();
            h.ctrlInitUserPoints(startPoints);
        } catch (BadInitException e) {
            throwBadInit(e.getMessage());
        }

    }

    // Helpers ----------------------------------------------------------

    private String getRestaurant(String restaurantName) {
        UDDINaming uddiNaming = endpointManager.getUddiNaming();
        String restaurant;
        try {
            restaurant = uddiNaming.lookup(restaurantName);
        } catch (UDDINamingException e) {
            throw new RuntimeException();
        }

        return restaurant;
    }


    private Collection<UDDIRecord> getRestaurants() {
        UDDINaming uddiNaming = endpointManager.getUddiNaming();
        Collection<UDDIRecord> restaurants;
        try {
            restaurants = uddiNaming.listRecords("T02_Restaurant%");
        } catch (UDDINamingException e) {
            throw new RuntimeException();
        }

        return restaurants;
    }

    // View helpers ----------------------------------------------------------

    private FoodOrderItem buildFoodOrderItem(HubFoodOrderItem hfoi) {
        FoodOrderItem foi = new FoodOrderItem();
        foi.setFoodId(buildFoodId(hfoi.getFoodId()));
        foi.setFoodQuantity(hfoi.getFoodQuantity());
        return foi;
    }


    /**
     * Helper to convert a domain object to a view.
     */

    private List<Food> buildFoodList(List<HubFood> hubFoodList) {
        List<Food> foodList = new ArrayList<>();

        for (HubFood hf : hubFoodList) {
            foodList.add(buildFood(hf));
        }

        return foodList;
    }

    private Food buildFood(HubFood hf) {
        Food f = new Food();
        f.setId(buildFoodId(hf.getId()));
        f.setEntree(hf.getEntree());
        f.setPlate(hf.getPlate());
        f.setDessert(hf.getDessert());
        f.setPrice(hf.getPrice());
        f.setPreparationTime(hf.getPreparationTime());
        return f;
    }

    private FoodId buildFoodId(HubFoodId hfi) {
        FoodId fi = new FoodId();
        fi.setMenuId(hfi.getMenuId());
        fi.setRestaurantId(hfi.getRestaurantId());
        return fi;
    }

    private HubFoodId buildHubFoodId(FoodId fi) {
        HubFoodId hfi = new HubFoodId(fi.getRestaurantId(), fi.getMenuId() );
        return hfi;
    }

    private HubFoodInit buildHubFoodInit(FoodInit fi) {
        HubFoodInit hfi = new HubFoodInit();
        HubFood hf = buildHubFood(fi.getFood());
        hfi.setHubFood(hf);
        hfi.setQuantity(fi.getQuantity());

        return hfi;
    }

    private HubFood buildHubFood(Food f) {
        HubFood hubFood = new HubFood();
        FoodId foodId = f.getId();
        hubFood.setId(buildHubFoodId(foodId));
        hubFood.setEntree(f.getEntree());
        hubFood.setPlate(f.getPlate());
        hubFood.setDessert(f.getDessert());
        hubFood.setPrice(f.getPrice());
        hubFood.setPreparationTime(f.getPreparationTime());
        return hubFood;
    }

    // Exception helpers -----------------------------------------------------

    /**
     * Helper to throw a new BadInit exception.
     */

    private void throwEmptyCartFault(final String message) throws EmptyCartFault_Exception {
        EmptyCartFault faultInfo = new EmptyCartFault();
        faultInfo.message = message;
        throw new EmptyCartFault_Exception(message, faultInfo);
    }


    private void throwInvalidUserIdInit(final String message) throws InvalidUserIdFault_Exception {
        InvalidUserIdFault faultInfo = new InvalidUserIdFault();
        faultInfo.message = message;
        throw new InvalidUserIdFault_Exception(message, faultInfo);
    }

    //InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception
    private void throwInvalidFoodIdFault(final String message) throws InvalidFoodIdFault_Exception {
        InvalidFoodIdFault faultInfo = new InvalidFoodIdFault();
        faultInfo.message = message;
        throw new InvalidFoodIdFault_Exception(message, faultInfo);
    }

    private void throwInvalidFoodQuantityFault(final String message) throws InvalidFoodQuantityFault_Exception {
        InvalidFoodQuantityFault faultInfo = new InvalidFoodQuantityFault();
        faultInfo.message = message;
        throw new InvalidFoodQuantityFault_Exception(message, faultInfo);
    }

    private void throwInvalidPoints(final String message) throws InvalidUserIdFault_Exception {
        InvalidUserIdFault faultInfo = new InvalidUserIdFault();
        faultInfo.message = message;
        throw new InvalidUserIdFault_Exception(message, faultInfo);
    }

    private void throwInvalidCC(final String message) throws InvalidCreditCardFault_Exception {
        InvalidCreditCardFault faultInfo = new InvalidCreditCardFault();
        faultInfo.message = message;
        throw new InvalidCreditCardFault_Exception(message, faultInfo);
    }

    private void throwInvalidMoney(final String message) throws InvalidMoneyFault_Exception {
        InvalidMoneyFault faultInfo = new InvalidMoneyFault();
        faultInfo.message = message;
        throw new InvalidMoneyFault_Exception(message, faultInfo);
    }

    private void throwBadInit(final String message) throws InvalidInitFault_Exception {
        InvalidInitFault faultInfo = new InvalidInitFault();
        faultInfo.message = message;
        throw new InvalidInitFault_Exception(message, faultInfo);
    }

    private void throwBadText(final String message) throws InvalidTextFault_Exception {
        InvalidTextFault faultInfo = new InvalidTextFault();
        faultInfo.message = message;
        throw new InvalidTextFault_Exception(message, faultInfo);
    }

    private void throwNotEnoughPointsFault(final String message) throws NotEnoughPointsFault_Exception {
        NotEnoughPointsFault faultInfo = new NotEnoughPointsFault();
        faultInfo.message = message;
        throw new NotEnoughPointsFault_Exception(message, faultInfo);
    }

    private void throwInvalidUserIdFault(final String message) throws InvalidUserIdFault_Exception {
        InvalidUserIdFault faultInfo = new InvalidUserIdFault();
        faultInfo.message = message;
        throw new InvalidUserIdFault_Exception(message, faultInfo);
    }
}
