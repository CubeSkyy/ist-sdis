
package com.forkexec.hub.domain;

public class HubFoodOrderItem {

    protected HubFoodId foodId;
    protected int foodQuantity;

    public HubFoodId getFoodId() {
        return foodId;
    }

    /**
     * Sets the value of the foodId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FoodId }
     *     
     */
    public void setFoodId(HubFoodId value) {
        this.foodId = value;
    }

    /**
     * Gets the value of the foodQuantity property.
     * 
     */
    public int getFoodQuantity() {
        return foodQuantity;
    }

    /**
     * Sets the value of the foodQuantity property.
     * 
     */
    public void setFoodQuantity(int value) {
        this.foodQuantity = value;
    }

}
