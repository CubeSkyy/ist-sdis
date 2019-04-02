package com.forkexec.rst.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import com.forkexec.rst.domain.Restaurant;
import com.forkexec.rst.domain.RestaurantMenu;
import com.forkexec.rst.domain.RestaurantMenuId;
import com.forkexec.rst.domain.RestaurantMenuOrder;
import com.forkexec.rst.domain.RestaurantMenuOrderId;


/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.rst.ws.RestaurantPortType",
        wsdlLocation = "RestaurantService.wsdl",
        name = "RestaurantWebService",
        portName = "RestaurantPort",
        targetNamespace = "http://ws.rst.forkexec.com/",
        serviceName = "RestaurantService"
)
public class RestaurantPortImpl implements RestaurantPortType {

    /**
     * The Endpoint manager controls the Web Service instance during its whole
     * lifecycle.
     */
    private RestaurantEndpointManager endpointManager;

    /**
     * Constructor receives a reference to the endpoint manager.
     */
    public RestaurantPortImpl(RestaurantEndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    // Main operations -------------------------------------------------------

    @Override
    public Menu getMenu(MenuId menuId) throws BadMenuIdFault_Exception {

        if (menuId == null || menuId.getId().trim().length() == 0)
            throwBadMenuIdFault("ID de menu invalido!");

        Restaurant r = Restaurant.getInstance();
        RestaurantMenu rm = r.getMenu(new RestaurantMenuId(menuId.getId()));

        Menu m = buildMenu(rm);

        return m;
    }

    @Override
    public List<Menu> searchMenus(String descriptionText) throws BadTextFault_Exception {
        if (descriptionText == null
                || descriptionText.trim().length() == 0 || descriptionText.contains(" "))
            throwBadTextFault("O texto de procura é invalido!");

        Restaurant r = Restaurant.getInstance();
        List<Menu> tempList = new ArrayList<>();
        for(RestaurantMenu menu: r.searchMenus(descriptionText)){
            tempList.add(buildMenu(menu));
        }

        return tempList;
    }

    @Override
    public MenuOrder orderMenu(MenuId arg0, int arg1)
            throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        
         if(arg0 == null || arg0.getId().trim().length()==0)
            throwBadMenuIdFault("ID de menu invalido!");

    	Restaurant r = Restaurant.getInstance();
    	RestaurantMenu rm = r.getMenu(new RestaurantMenuId(arg0.getId()));

    	if(rm.getQuantity() < arg1) {
    		throwInsufficientQuantityFault("Nao existe quantidade suficiente destes menus no restaurante!");
    	}

    	if(rm.getQuantity() <= 0) {
    		throwBadQuantityFault("As quantidades tem de ser positivas!");
    	}

        RestaurantMenuOrder result = r.orderMenu(new RestaurantMenuId(arg0.getId()), arg1);
    	return buildMenuOrder(result);
        
    }


    // Control operations ----------------------------------------------------

    /**
     * Diagnostic operation to check if service is running.
     */
    @Override
    public String ctrlPing(String inputMessage) {
        // If no input is received, return a default name.
        if (inputMessage == null || inputMessage.trim().length() == 0)
            inputMessage = "friend";

        // If the park does not have a name, return a default.
        String wsName = endpointManager.getWsName();
        if (wsName == null || wsName.trim().length() == 0)
            wsName = "Restaurant";

        // Build a string with a message to return.
        StringBuilder builder = new StringBuilder();
        builder.append("Hello ").append(inputMessage);
        builder.append(" from ").append(wsName);
        return builder.toString();
    }

    /**
     * Return all variables to default values.
     */
    @Override
    public void ctrlClear() {
    	Restaurant r = Restaurant.getInstance();
        r.resetState();
    }

    /**
     * Set variables with specific values.
     */
    @Override
    public void ctrlInit(List<MenuInit> initialMenus) throws BadInitFault_Exception {
        // TODO Auto-generated method stub
        if (initialMenus == null || initialMenus.isEmpty()) throwBadInit("Lista de Menus Invalida!");

        Restaurant r = Restaurant.getInstance();
        r.clearMenus();
        for (MenuInit mi : initialMenus) {
            Menu m = mi.getMenu();
            r.addMenu(new RestaurantMenu(
                    new RestaurantMenuId(m.getId().getId()),
                    m.getEntree(),
                    m.getPlate(),
                    m.getDessert(),
                    mi.getQuantity(),
                    m.getPrice(),
                    m.getPreparationTime()
            ));
        }

    }

    // View helpers ----------------------------------------------------------

    private MenuId buildMenuId(RestaurantMenuId rmi) {
        MenuId mi = new MenuId();
        mi.setId(rmi.getId());
        return mi;
    }

    private Menu buildMenu(RestaurantMenu rm) {
        Menu m = new Menu();

        m.setId(buildMenuId(rm.getId()));
        m.setEntree(rm.getEntree());
        m.setPlate(rm.getPlate());
        m.setDessert(rm.getDessert());
        m.setPrice(rm.getPrice());
        m.setPreparationTime(rm.getPreparationTime());

        return m;
    }

    private MenuOrderId buildMenuOrderId(RestaurantMenuOrderId rmoi){
        MenuOrderId moi = new MenuOrderId();
        moi.setId(rmoi.getId());
        return moi;
    }

    private MenuOrder buildMenuOrder(RestaurantMenuOrder rmo){

        MenuOrder mo = new MenuOrder();

        mo.setId(buildMenuOrderId(rmo.getId()));
        mo.setMenuId(buildMenuId(rmo.getRestaurantMenuId()));
        mo.setMenuQuantity(rmo.getMenuQuantity());

        return mo;
    }


    // Exception helpers -----------------------------------------------------

    /**
     * Helper to throw a new BadInit exception.
     */
    private void throwBadInit(final String message) throws BadInitFault_Exception {
        BadInitFault faultInfo = new BadInitFault();
        faultInfo.message = message;
        throw new BadInitFault_Exception(message, faultInfo);
    }

    private void throwBadMenuIdFault(final String message) throws BadMenuIdFault_Exception {
        BadMenuIdFault faultInfo = new BadMenuIdFault();
        faultInfo.message = message;
        throw new BadMenuIdFault_Exception(message, faultInfo);
    }

    private void throwBadTextFault(final String message) throws BadTextFault_Exception {
        BadTextFault faultInfo = new BadTextFault();
        faultInfo.message = message;
        throw new BadTextFault_Exception(message, faultInfo);
    }

    private void throwInsufficientQuantityFault(final String message) throws InsufficientQuantityFault_Exception {
    	InsufficientQuantityFault faultInfo = new InsufficientQuantityFault();
    	faultInfo.message = message;
    	throw new InsufficientQuantityFault_Exception(message, faultInfo);
    }

    private void throwBadQuantityFault(final String message) throws BadQuantityFault_Exception {
    	BadQuantityFault faultInfo = new BadQuantityFault();
    	faultInfo.message = message;
    	throw new BadQuantityFault_Exception(message, faultInfo);
    }

}
