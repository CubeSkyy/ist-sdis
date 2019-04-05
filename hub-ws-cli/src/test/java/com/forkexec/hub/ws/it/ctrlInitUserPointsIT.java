package com.forkexec.hub.ws.it;


import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class ctrlInitUserPointsIT extends BaseIT {

    @Test
    public void success() throws InvalidUserIdFault_Exception, InvalidInitFault_Exception {
        int NEW_INIT_POINTS = 120;
        client.ctrlInitUserPoints(NEW_INIT_POINTS);
        client.activateAccount(VALID_EMAIL);
        Assert.assertEquals(client.accountBalance(VALID_EMAIL),NEW_INIT_POINTS);
    }



    @Test(expected = InvalidInitFault_Exception.class)
    public void invalidEmail() throws InvalidInitFault_Exception {
        int INVALID_INIT_POINTS = -120;
        client.ctrlInitUserPoints(INVALID_INIT_POINTS);
    }
}
