//cant test without Ricardo's ctrlInitFoods

package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.*;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class searchDealIT extends BaseIT {

    @Test
    public void success() throws InvalidTextFault_Exception, InvalidInitFault_Exception{

    	client.ctrlInitFood(createFoodInitList());
        List<Food> list = client.searchDeal(DESCRIPTION_TEXT);

        List<Food> list_comp = new ArrayList<>();
        list_comp.add(createFood());
        list_comp.add(createFood2());

        Collections.sort(list_comp, new Comparator<Food>() {
        public int compare(Food f1, Food f2) {
            if(f1.getPrice() > f2.getPrice()){
                return 1;
            }
            else if(f1.getPrice() == f2.getPrice()){
                return 0;
            }
            else 
                return -1;
            }
        });

        Assert.assertEquals(list.get(0).getId().getMenuId(), list_comp.get(0).getId().getMenuId());
        Assert.assertEquals(list.get(1).getId().getMenuId(), list_comp.get(1).getId().getMenuId());
    }

    @Test(expected=InvalidTextFault_Exception.class)
    public void searchDeal_badDescription() throws InvalidTextFault_Exception{
    	 client.searchDeal(EMPTY_STRING);
    }

}