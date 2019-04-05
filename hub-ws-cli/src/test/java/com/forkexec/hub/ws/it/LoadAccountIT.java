package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class LoadAccountIT extends BaseIT {
    private String VALID_EMAIL = "testtest@test.com";
    private String VALID_FAKE_CC_NUMBER = "5105105105105100";

    @Test
    public void success() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
        final Integer  MONEY_TO_ADD = 10;
        final Integer  POINTS_TO_ADD = 1000;

        client.activateAccount(VALID_EMAIL);
        client.loadAccount(VALID_EMAIL,MONEY_TO_ADD,VALID_FAKE_CC_NUMBER);
        Assert.assertEquals(client.accountBalance(VALID_EMAIL), DEFAULT_INITIAL_BALANCE + POINTS_TO_ADD);

    }
}
