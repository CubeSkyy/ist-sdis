package com.forkexec.rst.ws;

import java.util.List;

import javax.jws.WebService;

import com.forkexec.rst.domain.Restaurant;
import com.forkexec.rst.domain.RestaurantMenu;
import com.forkexec.rst.domain.RestaurantMenuId;


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

        if(menuId == null || menuId.getId().trim().length()==0)
            throwBadMenuIdFault("ID de menu invalido!");

        Restaurant r = Restaurant.getInstance();
        RestaurantMenu rm = r.getMenu(new RestaurantMenuId(menuId.getId()));

        Menu m = buildMenu(rm);

        return m;
    }

    @Override
    public List<Menu> searchMenus(String descriptionText) throws BadTextFault_Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MenuOrder orderMenu(MenuId arg0, int arg1)
            throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        // TODO Auto-generated method stub
        return null;
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

}
