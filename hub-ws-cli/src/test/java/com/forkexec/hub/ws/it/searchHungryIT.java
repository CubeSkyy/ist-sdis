package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.*;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;
import java.util.ArrayList;

public class searchHungryIT extends BaseIT {

    @Test
    public void success() throws InvalidTextFault_Exception{

    	client.ctrlInitFoods(createFoodInitList());
    	List<Food> list = new ArrayList<>();
        list = client.searchHungry(DESCRIPTION_TEXT);
        Food first = list.get(0);

        List<Food> list_comp = new ArrayList<>();
        list_comp.add(createFoodInit());
        list_comp.add(createFoodInit2());

        Collections.sort(list_comp, new Comparator<Food>() {
    	public int compare(Food f1, Food f2) {
        return f1.getPreparationTime().compareTo(s2.getPreparationTime());}
		});

		Assert.assertEquals(list.get(0), list_comp.get(0));
		Assert.assertEquals(list.get(1), list_comp.get(1));		
    }

    @Test(expected=InvalidTextFault_Exception.class)
    public void searchHungry_badDescription() throws InvalidTextFault_Exception{
    	 client.searchHungry(EMPTY_STRING);
    }

}