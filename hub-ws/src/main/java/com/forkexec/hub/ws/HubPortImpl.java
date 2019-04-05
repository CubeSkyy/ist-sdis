package com.forkexec.hub.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.jws.WebService;
import javax.xml.soap.SOAPFault;

import com.forkexec.hub.domain.*;

import com.forkexec.rst.ws.BadInitFault;
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
        try{
            h.activateAccount(userId);
        } catch (InvalidEmailException e){
            throwInvalidUserIdInit("O email e invalido!");
        }
    }

    @Override
    public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
            throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
        // TODO Auto-generated method stub
        // Encaminhar para o Points

    }


    @Override
    public List<Food> searchDeal(String description) throws InvalidTextFault_Exception {
        // TODO return lowest price menus first
        return null;
    }

    @Override
    public List<Food> searchHungry(String description) throws InvalidTextFault_Exception {
        // TODO return lowest preparation time first
        return null;
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
        HubFoodId hib = new HubFoodId(foodId.getRestaurantId(), foodId.getMenuId());

        HubFoodOrder hfo = h.getFoodCart(userId);

        if(hfo == null)
            hfo = h.createFoodCart(userId);

        List<HubFoodOrderItem> listItem = hfo.getItems();

        HubFoodOrderItem hfoi = listItem.stream().filter(i -> i.getFoodId().equals(hib)).findAny().orElse(null);

        if(hfoi == null)
            listItem.add(new HubFoodOrderItem(hib,foodQuantity));
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
        HubFoodOrder hubOrder = h.getFoodCart(userId);

        List<HubFoodOrderItem> listItem = hubOrder.getItems();

        if(listItem.size()==0)
            throwEmptyCartFault("Carrinho vazio!");

        int totalPoints = listItem.stream().mapToInt(h::getPoints).sum();
        /* USO da função ACCOUNT BALANCE
         *  TODO: Trocar para função interna
         */
        if(totalPoints < accountBalance(userId))
            throwNotEnoughPointsFault("Não tem saldo suficiente!");

        FoodOrderId foi = new FoodOrderId();
        foi.setId(hubOrder.getFoodOrderId().getId());
        FoodOrder fo = new FoodOrder();
        fo.setFoodOrderId(foi);

        List<FoodOrderItem> listFoodOrderItems = fo.getItems();
        listItem.forEach(hubFoodItem -> listFoodOrderItems.add(buildFoodOrderItem(hubFoodItem)));

        /* Funcao que vai retirar o saldo ao utilizador e abater o stock dos restaurantes */
        h.confirmOrder(userId, totalPoints);

        return fo;
    }

    @Override
    public int accountBalance(String userId) throws InvalidUserIdFault_Exception {
        // TODO
        return 0;
    }

    @Override
    public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception {
        // TODO
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception {
        if(userId == null || userId.trim().length()==0)
            throwInvalidUserIdInit("User ID invalido!");

        Hub h = Hub.getInstance();

        HubFoodOrder hubOrder = h.getFoodCart(userId);

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

        try{
            UDDINaming uddiNaming = endpointManager.getUddiNaming();
            restaurants = uddiNaming.listRecords("T02_Restaurant%");
            for(UDDIRecord ws : restaurants){
                RestaurantClient client = new RestaurantClient(ws.getUrl());

                builder.append(client.ctrlPing("Cliente")).append("\n");
            }
            if (restaurants.size() == 0){
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

    }

    /**
     * Set variables with specific values.
     */
    @Override
    public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
        /*if(startPoints < 0){
            throwInvalidInit("O numero de pontos nao pode ser negativo!");
        }*/

        //Aqui basta aceder ao points e fazer ctrlInit


    }


    // View helpers ----------------------------------------------------------
    private FoodId buildFoodId(HubFoodId id){
        FoodId foi = new FoodId();
        foi.setId(id.getId());
        return foi;
    }
    private FoodOrderItem buildFoodOrderItem(HubFoodOrderItem hfoi){
        FoodOrderItem foi = new FoodOrderItem();
        foi.setFoodId(buildFoodId(hfoi.getFoodId()));
        foi.setFoodQuantity(hfoi.getFoodQuantity());
        return foi;
    }


     /** Helper to convert a domain object to a view. */
    /* private ParkInfo buildParkInfo(Park park) {
     ParkInfo info = new ParkInfo();
     info.setId(park.getId());
     info.setCoords(buildCoordinatesView(park.getCoordinates()));
     info.setCapacity(park.getMaxCapacity());
     info.setFreeSpaces(park.getFreeDocks());
     info.setAvailableCars(park.getAvailableCars());
     return info;
     }*/

    // Exception helpers -----------------------------------------------------

    /** Helper to throw a new BadInit exception. */

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

    private void throwNotEnoughPointsFault(final String message) throws NotEnoughPointsFault_Exception {
        NotEnoughPointsFault faultInfo = new NotEnoughPointsFault();
        faultInfo.message = message;
        throw new NotEnoughPointsFault_Exception(message, faultInfo);
    }


}
