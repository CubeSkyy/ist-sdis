package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointsBalanceIT extends BaseIT {

    @Test
    public void success() {
        try {
            client.activateUser(EMAIL);
            client.addPoints(EMAIL, 100);
            assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE + 100);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void invalidEmail() throws InvalidEmailFault_Exception {
        client.pointsBalance(INVALID_EMAIL);
    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void validEmail() throws InvalidEmailFault_Exception {
        client.pointsBalance(EMAIL);
    }

}
