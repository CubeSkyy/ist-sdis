//O teste falha porque a ctrlInitFood ainda nao esta implementada

package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.*;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;
import java.util.ArrayList;

public class searchDealIT extends BaseIT {

    @Test
    public void success() throws InvalidTextFault_Exception{

    	client.ctrlInitFoods(createFoodInitList());
    	List<Food> list = new ArrayList<>();
        list = client.searchDeal(DESCRIPTION_TEXT);
        Food first = list.get(0);

        for(Food f: list){
        	Assert.assertTrue(f.getPrice() >= first.getPrice());
        }
    }

    @Test(expected=InvalidTextFault_Exception.class)
    public void searchDeal_badDescription() throws InvalidTextFault_Exception{
    	 client.searchDeal(EMPTY_STRING);
    }

}