package com.forkexec.hub.ws.it;

import com.forkexec.hub.ws.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ctrlClearIT extends BaseIT {
    @Test
    public void success() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {

        client.activateAccount("joaomaria@gmail.pt");
        client.loadAccount("joaomaria@gmail.pt", 10, VALID_FAKE_CC_NUMBER);
        client.ctrlClear();
        client.activateAccount("joaomaria@gmail.pt");
        Assert.assertEquals(client.accountBalance("joaomaria@gmail.pt"), DEFAULT_INITIAL_BALANCE);

    }

}