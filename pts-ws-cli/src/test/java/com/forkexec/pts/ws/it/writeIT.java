package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class writeIT extends BaseIT {


    @Test
    public void success() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception {
        client.activateUser(EMAIL);
        client.write(EMAIL, 10);
        assertEquals(client.pointsBalance(EMAIL),  10);
    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void InvalidEmail_emptyString() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {

        client.write(EMPTY_STRING, 10);
    }

    @Test(expected = InvalidPointsFault_Exception.class)
    public void InvalidPoints_negative() throws InvalidPointsFault_Exception, InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception {
        client.activateUser(EMAIL);
        client.write(EMAIL, -10);
    }
}
