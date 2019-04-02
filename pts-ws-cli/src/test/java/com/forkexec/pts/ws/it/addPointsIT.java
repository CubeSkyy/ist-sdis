package com.forkexec.pts.ws.it;


import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class addPointsIT extends BaseIT {

    private static final int NEW_SCORE = 420;

    @Test
    public void success() {
        try {
            client.activateUser(EMAIL);
            client.addPoints(EMAIL,NEW_SCORE);
            assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE + NEW_SCORE);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void InvalidEmail_wrongFormat() throws InvalidEmailFault_Exception{
        try {
            client.addPoints(INVALID_EMAIL,NEW_SCORE);
        }catch (InvalidEmailFault_Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = InvalidEmailFault_Exception.class)
    public void InvalidEmail_emptyString() throws InvalidEmailFault_Exception{
        try {
            client.addPoints(EMPTY_STRING,NEW_SCORE);
        }catch (InvalidEmailFault_Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = InvalidPointsFault_Exception.class)
    public void InvalidPoints_zero() throws InvalidPointsFault_Exception{
        try {
            client.activateUser(EMAIL);
            client.addPoints(EMAIL,0);
        }catch (InvalidPointsFault_Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = InvalidPointsFault_Exception.class)
    public void InvalidPoints_negative() throws InvalidPointsFault_Exception{
        try {
            client.activateUser(EMAIL);
            client.addPoints(EMAIL,-1);
        }catch (InvalidPointsFault_Exception e){
            Assert.fail(e.getMessage());
        }
    }
}
