package com.forkexec.hub.ws.it;
import com.forkexec.hub.ws.*;
import org.junit.Test;

import java.util.List;

public class ctrlClearIT extends BaseIT {

    @Test(expected=InvalidUserIdFault_Exception.class)
    public void success() throws InvalidUserIdFault_Exception {

		client.ctrlInitUserPoints(100);
		client.activateAccount("joaomaria@gmail.pt");
        client.ctrlClear();
        client.accountBalance("joaomaria@gmail.pt");
    }

}