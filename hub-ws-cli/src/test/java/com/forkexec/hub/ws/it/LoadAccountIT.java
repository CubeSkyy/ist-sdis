package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class LoadAccountIT extends BaseIT {


    @Test
    public void success() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {


        client.activateAccount(VALID_EMAIL);
        client.loadAccount(VALID_EMAIL,MONEY_TO_ADD,VALID_FAKE_CC_NUMBER);
        Assert.assertEquals(client.accountBalance(VALID_EMAIL), DEFAULT_INITIAL_BALANCE + POINTS_TO_ADD);
    }


    @Test(expected = InvalidCreditCardFault_Exception.class)
    public void InvalidCreditCard() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
        final int  MONEY_TO_ADD = 10;
        client.activateAccount(VALID_EMAIL);
        client.loadAccount(VALID_EMAIL,MONEY_TO_ADD, INVALID_FAKE_CC_NUMBER);
    }

    @Test(expected = InvalidMoneyFault_Exception.class)
    public void  InvalidMoney() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
        final int  MONEY_TO_ADD = -10;
        client.activateAccount(VALID_EMAIL);
        client.loadAccount(VALID_EMAIL,MONEY_TO_ADD, VALID_FAKE_CC_NUMBER);
    }

    @Test(expected = InvalidUserIdFault_Exception.class)
    public void  InvalidUserId() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
        final int  MONEY_TO_ADD = 50;
        client.loadAccount(INVALID_EMAIL,MONEY_TO_ADD, VALID_FAKE_CC_NUMBER);
    }

}
