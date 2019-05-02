package com.forkexec.pts.ws.it;


import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ctrlClearIT extends BaseIT {

    private static final int NEW_SCORE = 420;

    @Test
    public void success() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
            client.ctrlClear();
            client.activateUser(EMAIL);
            assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE);
    }

    @Test(expected = InvalidEmailFault_Exception.class) 
    public void ctrlClear_noUser() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception{
    	client.activateUser(EMAIL);
    	client.ctrlClear();
    	client.pointsBalance(EMAIL);
    }
}
