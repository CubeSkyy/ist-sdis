package com.forkexec.hub.ws.it;


import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class AccountBalanceIT extends BaseIT {


    @Test
    public void success() throws InvalidUserIdFault_Exception {
        client.activateAccount(VALID_EMAIL);
        Assert.assertEquals(client.accountBalance(VALID_EMAIL), DEFAULT_INITIAL_BALANCE);

    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void invalidEmail() throws InvalidUserIdFault_Exception {
        client.accountBalance(INVALID_EMAIL);
    }
}
