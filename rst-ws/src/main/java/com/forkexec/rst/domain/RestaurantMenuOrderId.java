package com.forkexec.rst.domain;   

public class RestaurantMenuOrderId{
    protected String _id;

    public RestaurantMenuOrderId(String id){
        setId(id);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return _id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this._id = value;
    }
}