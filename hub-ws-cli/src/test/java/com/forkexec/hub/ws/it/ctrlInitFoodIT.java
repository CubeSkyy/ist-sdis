package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ctrlInitFoodIT extends BaseIT {

	@Test
	public void success() {
		client.ctrlInitFoodIT(createFoodInitList());

		//todo, waiting for ricardo's function
	}

}