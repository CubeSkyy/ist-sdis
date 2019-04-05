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
    	List<Food> list = new ArrayList<>();
        list = client.searchDeal(DESCRIPTION_TEXT);
        Food first = list.get(0);

        List<Food> list_comp = new ArrayList<>();
        list_comp.add(createFood());
        list_comp.add(createFood2());

        Collections.sort(list_comp, new Comparator<Food>() {
        public int compare(Food f1, Food f2) {
            if(f1.getPreparationTime() > f2.getPreparationTime()){
                return 1;
            }
            else if(f1.getPreparationTime() == f2.getPreparationTime()){
                return 0;
            }
            else 
                return -1;
            }
        });

        Assert.assertEquals(list.get(0), list_comp.get(0));
        Assert.assertEquals(list.get(1), list_comp.get(1));     
    }

    @Test(expected=InvalidTextFault_Exception.class)
    public void searchDeal_badDescription() throws InvalidTextFault_Exception{
    	 client.searchDeal(EMPTY_STRING);
    }

}