package com.forkexec.pts.ws.it;


import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ctrlClearIT extends BaseIT {

    private static final int NEW_SCORE = 420;

    @Test
    public void success() {
        try {
            client.ctrlClear();
            client.activateUser(EMAIL);
            assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }
}
