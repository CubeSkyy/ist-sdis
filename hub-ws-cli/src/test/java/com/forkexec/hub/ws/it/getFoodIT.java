package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertNotNull;
import com.forkexec.hub.ws.*;

import org.junit.Test;


public class getFoodIT extends BaseIT {

	@Test
	public void success() throws InvalidFoodIdFault_Exception , InvalidInitFault_Exception {
		client.ctrlInitFood(createFoodInitList());
		client.getFood(foodTestId);
	}

	@Test(expected=InvalidFoodIdFault_Exception.class)
	public void getFood_Null() throws InvalidFoodIdFault_Exception, InvalidInitFault_Exception{
		client.ctrlInitFood(createFoodInitList());
		client.getFood(null);
	}

	@Test(expected=InvalidFoodIdFault_Exception.class)
	public void getFood_Invalid() throws InvalidFoodIdFault_Exception, InvalidInitFault_Exception{
		client.ctrlInitFood(createFoodInitList());

		FoodId test = new FoodId();
		test.setRestaurantId("T02_Restaurant1");
		test.setMenuId("Menu33");
		client.getFood(test);
	}

}
