package com.forkexec.hub.ws.it;


import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class ctrlInitUserPointsIT extends BaseIT {
    private int NEW_INIT_POINTS = 120;
    private int INVALID_INIT_POINTS = -120;

    @Test
    public void success() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidInitFault_Exception {
        client.ctrlInitUserPoints(NEW_INIT_POINTS);
        client.activateAccount(VALID_EMAIL);
        Assert.assertEquals(client.accountBalance(VALID_EMAIL),NEW_INIT_POINTS);
    }


    @Test
    public void success_changeUserWallets() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidInitFault_Exception {
        client.activateAccount(VALID_EMAIL);
        client.loadAccount(VALID_EMAIL,MONEY_TO_ADD,VALID_FAKE_CC_NUMBER);
        Assert.assertEquals(client.accountBalance(VALID_EMAIL), DEFAULT_INITIAL_BALANCE + POINTS_TO_ADD);

        client.ctrlInitUserPoints(NEW_INIT_POINTS);
        Assert.assertEquals(client.accountBalance(VALID_EMAIL), NEW_INIT_POINTS);


    }

    @Test(expected = InvalidInitFault_Exception.class)
    public void invalidEmail() throws InvalidInitFault_Exception {
        client.ctrlInitUserPoints(INVALID_INIT_POINTS);
    }
}
