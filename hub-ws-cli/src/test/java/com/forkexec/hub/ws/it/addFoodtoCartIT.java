package com.forkexec.hub.ws.it;

import static org.junit.Assert.*;
import com.forkexec.hub.ws.*;

import org.junit.Test;


public class addFoodtoCartIT extends BaseIT {

	@Test
	public void success() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception, InvalidInitFault_Exception {
		client.ctrlInitUserPoints(100);
		client.ctrlInitFood(createFoodInitList());
		client.activateAccount(VALID_EMAIL);
		client.addFoodToCart(VALID_EMAIL, createFoodId(), 3);
		Assert.assertNotNull(client.cartContents(VALID_EMAIL));
	}

	@Test(expected=InvalidFoodIdFault_Exception.class)
	public void addFoodtoCart_InvalidFoodId() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception, InvalidInitFault_Exception {
		client.ctrlInitFood(createFoodInitList());
		client.activateAccount(VALID_EMAIL);
		client.addFoodToCart(VALID_EMAIL, null, 3);
	}

	@Test(expected=InvalidFoodQuantityFault_Exception.class)
	public void addFoodtoCart_InvalidFoodQuant() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception, InvalidInitFault_Exception {
		client.ctrlInitFood(createFoodInitList());
		client.activateAccount(VALID_EMAIL);
		client.addFoodToCart(VALID_EMAIL, foodTestId, -3);
	}

	@Test(expected=InvalidUserIdFault_Exception.class)
	public void addFoodtoCart_InvalidUserId() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception, InvalidInitFault_Exception {
		client.ctrlInitFood(createFoodInitList());
		client.activateAccount(VALID_EMAIL);
		client.addFoodToCart(null, foodTestId, 3);
	}

}
