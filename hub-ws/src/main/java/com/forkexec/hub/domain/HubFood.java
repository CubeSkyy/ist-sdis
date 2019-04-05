package com.forkexec.hub.domain;
import java.util.Comparator;

public class HubFood {

    protected HubFoodId id;
    protected String entree;
    protected String plate;
    protected String dessert;
    protected int price;
    protected int preparationTime;


    public static Comparator<HubFood> priceComparator = (f1, f2) -> {
        Integer p1 = f1.getPrice();
        Integer p2 = f2.getPrice();

        return p1.compareTo(p2);
    };

    public static Comparator<HubFood> preparationTimeComparator = (f1, f2) -> {
        Integer p1 = f1.getPreparationTime();
        Integer p2 = f2.getPreparationTime();

        return p1.compareTo(p2);
    };

    public HubFoodId getId() {
        return id;
    }


    public void setId(HubFoodId value) {
        this.id = value;
    }


    public String getEntree() {
        return entree;
    }


    public void setEntree(String value) {
        this.entree = value;
    }


    public String getPlate() {
        return plate;
    }


    public void setPlate(String value) {
        this.plate = value;
    }


    public String getDessert() {
        return dessert;
    }

    public void setDessert(String value) {
        this.dessert = value;
    }

    public int getPrice() {
        return price;
    }


    public void setPrice(int value) {
        this.price = value;
    }


    public int getPreparationTime() {
        return preparationTime;
    }


    public void setPreparationTime(int value) {
        this.preparationTime = value;
    }

}
