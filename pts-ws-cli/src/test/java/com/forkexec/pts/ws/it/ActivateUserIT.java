package com.forkexec.pts.ws.it;

import com.forkexec.hub.domain.InvalidEmailException;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault;
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
    public void emailAlreadyExists() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, InvalidEmailException {

        client.activateUser(EMAIL);
        client.activateUser(EMAIL);

    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void invalidEmail() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception {
        try{
            client.activateUser(INVALID_EMAIL);
        }catch (InvalidEmailException e){
            final InvalidEmailFault faultInfo = new InvalidEmailFault();
            faultInfo.setMessage(e.getMessage());
            throw new InvalidEmailFault_Exception(e.getMessage(), faultInfo);
        }

    }

}
