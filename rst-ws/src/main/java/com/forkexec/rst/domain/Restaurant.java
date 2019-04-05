package com.forkexec.rst.domain;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Objects;
 
/*
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.MenuOrder;
import com.forkexec.rst.ws.MenuOrderId;
*/

/**
 * Restaurant
 * <p>
 * A restaurant server.
 */
public class Restaurant {


    // Singleton -------------------------------------------------------------

    private Map<RestaurantMenuId, RestaurantMenu> menuMap = new ConcurrentHashMap<>();


    private int orderNumber = 1;


    /**
     * Private constructor prevents instantiation from other classes.
     */
    private Restaurant() {
        // Initialization of default values
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final Restaurant INSTANCE = new Restaurant();
    }

    public static synchronized Restaurant getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void setOrderNumber(int num) {
        orderNumber = num;
    }

    public void clearMenus() {
        menuMap.clear();
    }

    public void addMenu(RestaurantMenu m) {
        menuMap.put(m.getId(), m);
    }

    public RestaurantMenu getMenu(RestaurantMenuId mid) throws NoSuchMenuFaultException {
       RestaurantMenu m = menuMap.get(mid);

       if (m == null)
           throw new NoSuchMenuFaultException("Não existe menu com ID " + mid.getId() + "!");

        return m;
    }




    public List<RestaurantMenu> searchMenus(String descriptionText) {
        List<RestaurantMenu> tempList = new ArrayList<>();

        for (RestaurantMenu menu : menuMap.values()) {
            if (menu.getDessert().contains(descriptionText)
                    || menu.getEntree().contains(descriptionText)
                    || menu.getPlate().contains(descriptionText))
                tempList.add(menu);
        }
        return tempList;
    }

    public RestaurantMenuOrder orderMenu(RestaurantMenuId m_id, int quantity) throws NoSuchMenuFaultException {

        RestaurantMenu rm = getMenu(m_id);

        int oldQuantity = rm.getQuantity();
        rm.setQuantity(oldQuantity - quantity);

        RestaurantMenuOrderId m_order_id = new RestaurantMenuOrderId("PedidoNr" + orderNumber);
        orderNumber += 1;

        RestaurantMenuOrder m_order = new RestaurantMenuOrder(m_order_id, m_id, quantity);

        return m_order;
    }

    public void resetState() {
        clearMenus();
        setOrderNumber(1);
    }

}
