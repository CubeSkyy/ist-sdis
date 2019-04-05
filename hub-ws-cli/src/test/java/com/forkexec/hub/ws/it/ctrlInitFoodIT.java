package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertNotNull;
import com.forkexec.hub.ws.*;

import org.junit.Test;

public class ctrlInitFoodIT extends BaseIT {

	@Test
	public void success() throws InvalidInitFault_Exception {
		client.ctrlInitFood(createFoodInitList());
	}

	@Test(expected=InvalidInitFault_Exception.class)
	public void ctrlInitFood_Invalid() throws InvalidInitFault_Exception {
		client.ctrlInitFood(null);
	}

}