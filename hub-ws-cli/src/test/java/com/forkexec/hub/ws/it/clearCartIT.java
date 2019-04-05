package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;
import com.forkexec.hub.ws.InvalidFoodQuantityFault_Exception;

import org.junit.Assert;
import org.junit.Test;

public class clearCartIT extends BaseIT {

    @Test
    public void success() throws InvalidUserIdFault_Exception {
        try {
            client.ctrlInitFood(createFoodInitList());
            client.activateAccount(VALID_EMAIL);
            Food f = createFood();
            client.addFoodToCart(VALID_EMAIL,f.getId(),1);
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(client.cartContents(VALID_EMAIL).size(),1);
        client.clearCart(VALID_EMAIL);
        Assert.assertEquals(client.cartContents(VALID_EMAIL).size(),0);
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void invalidUserIdFault() throws InvalidUserIdFault_Exception {
        client.clearCart(INVALID_EMAIL);
    }
}
