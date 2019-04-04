
package com.forkexec.hub.domain;

public class HubFoodId {

    private String _restaurantId;
    private String _menuId;

    /**
     * Gets the value of the restaurantId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestaurantId() {
        return _restaurantId;
    }

    /**
     * Sets the value of the restaurantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestaurantId(String value) {
        this._restaurantId = value;
    }

    /**
     * Gets the value of the menuId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMenuId() {
        return _menuId;
    }

    /**
     * Sets the value of the menuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMenuId(String value) {
        this._menuId = value;
    }

    public boolean equals(Object o) {
        HubFoodId h = (HubFoodId) o;
        return h.getMenuId().equals(this.getMenuId());
    }

}
