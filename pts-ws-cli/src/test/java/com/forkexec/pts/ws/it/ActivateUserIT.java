package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivateUserIT extends BaseIT {

    @Test
    public void success() {
        try {
            client.activateUser(EMAIL);
            assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = EmailAlreadyExistsFault_Exception.class)
    public void emailAlreadyExists() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {

        client.activateUser(EMAIL);
        client.activateUser(EMAIL);

    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void invalidEmail() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception {
        client.activateUser(INVALID_EMAIL);
    }

}
