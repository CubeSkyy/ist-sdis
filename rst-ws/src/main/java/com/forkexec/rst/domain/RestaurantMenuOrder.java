package com.forkexec.rst.domain;

public class RestaurantMenuOrder {

    protected RestaurantMenuOrderId _id;
    protected RestaurantMenuId _rmenuId;
    protected int _menuQuantity;

    public RestaurantMenuOrder(RestaurantMenuOrderId id, RestaurantMenuId rmenuId, int menuQuantity) {
    	setId(id);
    	setRestaurantMenuId(rmenuId);
    	setMenuQuantity(menuQuantity);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link MenuOrderId }
     *     
     */
    public RestaurantMenuOrderId getId() {
        return _id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestaurantMenuOrderId }
     *     
     */
    public void setId(RestaurantMenuOrderId value) {
        this._id = value;
    }

    /**
     * Gets the value of the menuId property.
     * 
     * @return
     *     possible object is
     *     {@link RestaurantMenuId }
     *     
     */
    public RestaurantMenuId getRestaurantMenuId() {
        return _rmenuId;
    }

    /**
     * Sets the value of the menuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestaurantMenuId }
     *     
     */
    public void setRestaurantMenuId(RestaurantMenuId value) {
        this._rmenuId = value;
    }

    /**
     * Gets the value of the menuQuantity property.
     * 
     */
    public int getMenuQuantity() {
        return _menuQuantity;
    }

    /**
     * Sets the value of the menuQuantity property.
     * 
     */
    public void setMenuQuantity(int value) {
        this._menuQuantity = value;
    }
}