
package com.forkexec.hub.domain;

import java.util.ArrayList;
import java.util.List;

public class HubFoodOrder {

    protected HubFoodOrderId foodOrderId;
    protected List<HubFoodOrderItem> items = null;

    public HubFoodOrderId getFoodOrderId() {
        return foodOrderId;
    }

    public void setFoodOrderId(HubFoodOrderId value) {
        this.foodOrderId = value;
    }

    public List<HubFoodOrderItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return this.items;
    }

}
