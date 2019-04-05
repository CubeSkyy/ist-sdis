package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertNotNull;
import com.forkexec.hub.ws.*;

import org.junit.Test;


public class cartContentsIT extends BaseIT {

	@Test
	public void success() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlInitUserPoints(100);
		client.ctrlInitFood(createFoodInitList());
		client.activateAccount(VALID_EMAIL);
		client.cartContents(VALID_EMAIL);
	}

	@Test(expected=InvalidUserIdFault_Exception.class)
	public void cartContents_Invalid() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlInitUserPoints(100);
		client.ctrlInitFood(createFoodInitList());
		client.activateAccount(VALID_EMAIL);
		client.cartContents("");
	}


}