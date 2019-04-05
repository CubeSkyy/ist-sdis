package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.*;
import org.junit.Assert;
import org.junit.Test;

public class orderCartIT extends BaseIT {

    @Test
    public void success()
            throws EmptyCartFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
        try {
            client.ctrlInitFood(createFoodInitList());
            client.activateAccount(VALID_EMAIL);
            client.loadAccount(VALID_EMAIL, PRICE, VALID_FAKE_CC_NUMBER);
            Food f = createFood();
            client.addFoodToCart(VALID_EMAIL, f.getId(), 1);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        client.orderCart(VALID_EMAIL);

        Assert.assertEquals(client.accountBalance(VALID_EMAIL), 100);
    }



    @Test(expected = EmptyCartFault_Exception.class)
    public void emptyCartFault() throws EmptyCartFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
        try {
            client.activateAccount(VALID_EMAIL);
            client.loadAccount(VALID_EMAIL, PRICE, VALID_FAKE_CC_NUMBER);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        client.orderCart(VALID_EMAIL);
    }


    @Test(expected = InvalidUserIdFault_Exception.class)
    public void invalidUserIdFault() throws EmptyCartFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
        client.orderCart(INVALID_EMAIL);
    }


    @Test(expected = NotEnoughPointsFault_Exception.class)
    public void notEnoughPointsFault() throws EmptyCartFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
        try {
            client.ctrlInitFood(createFoodInitList());
            client.activateAccount(VALID_EMAIL);
            Food f = createFood();
            client.addFoodToCart(VALID_EMAIL, f.getId(), 1);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        client.orderCart(VALID_EMAIL);
    }
}
