package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointsBalanceIT extends BaseIT {

    @Test
    public void success() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        client.activateUser(EMAIL);
        client.addPoints(EMAIL, 100);
        assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE + 100);
    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void invalidEmail() throws InvalidEmailFault_Exception {
        client.pointsBalance(INVALID_EMAIL);
    }

    @Test
    public void validEmail() throws InvalidEmailFault_Exception {
        client.pointsBalance(EMAIL);
    }

}
