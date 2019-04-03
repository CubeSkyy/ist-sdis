package com.forkexec.rst.domain;

public class RestaurantMenu {

    protected RestaurantMenuId _id;
    protected String _entree;
    protected String _plate;
    protected String _dessert;
    protected int _quantity;
    protected int _price;
    protected int _preparationTime;


    public RestaurantMenu(RestaurantMenuId id,
                          String entree,
                          String plate,
                          String dessert,
                          int quantity,
                          int price,
                          int preparationTime)
    {
        setId(id);
        setPlate(plate);
        setEntree(entree);
        setDessert(dessert);
        setQuantity(quantity);
        setPrice(price);
        setPreparationTime(preparationTime);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link RestaurantMenuId }
     *     
     */
    public RestaurantMenuId getId() {
        return _id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestaurantMenuId }
     *     
     */
    public void setId(RestaurantMenuId value) {
        this._id = value;
    }

    /**
     * Gets the value of the entree property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntree() {
        return _entree;
    }

    /**
     * Sets the value of the entree property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    private void setEntree(String value) {
        this._entree = value;
    }

    /**
     * Gets the value of the plate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlate() {
        return _plate;
    }

    /**
     * Sets the value of the plate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    private void setPlate(String value) {
        this._plate = value;
    }

    /**
     * Gets the value of the dessert property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDessert() {
        return _dessert;
    }

    /**
     * Sets the value of the dessert property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    private void setDessert(String value) {
        this._dessert = value;
    }

    /**
     * Gets the value of the price property.
     * 
     */
    public int getPrice() {
        return _price;
    }

    /**
     * Sets the value of the price property.
     * 
     */
    private void setPrice(int value) {
        this._price = value;
    }

    /**
     * Gets the value of the preparationTime property.
     * 
     */
    public int getPreparationTime() {
        return _preparationTime;
    }

    /**
     * Sets the value of the preparationTime property.
     * 
     */
    private void setPreparationTime(int value) {
        this._preparationTime = value;
    }

    public int getQuantity(){ return _quantity;}

    public void setQuantity(int newQuantity) { _quantity = newQuantity;}


}
