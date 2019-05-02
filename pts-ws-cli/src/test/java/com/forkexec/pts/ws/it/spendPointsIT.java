package com.forkexec.pts.ws.it;


import com.forkexec.hub.domain.InvalidEmailException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.cli.NotEnoughBalanceException;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;


public class spendPointsIT extends BaseIT {

    @Test
    public void success() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceException, EmailAlreadyExistsFault_Exception, InvalidEmailException{

      	client.activateUser(EMAIL);
      	client.spendPoints(EMAIL, 30);

      	assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE - 30);
    }

    @Test (expected = InvalidEmailFault_Exception.class)
    public void InvalidEmail_NULL() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceException {
    	client.spendPoints(null, 30);
    }

    @Test (expected = InvalidEmailFault_Exception.class)
    public void InvalidEmail_WrongFormat() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceException {
    	client.spendPoints("test", 30);
    }

    @Test (expected = InvalidEmailFault_Exception.class)
    public void InvalidEmail_Empty() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceException{
    	client.spendPoints("", 30);
    }

    @Test (expected = InvalidPointsFault_Exception.class)
    public void InvalidPoints_0() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceException{
    	client.spendPoints(EMAIL, 0);
    }

    @Test (expected = InvalidPointsFault_Exception.class)
    public void InvalidPoints_Negative() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceException{
    	client.spendPoints(EMAIL, -20);
    }

    @Test (expected = NotEnoughBalanceException.class)
    public void NotEnoughBalance() throws NotEnoughBalanceException, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, InvalidPointsFault_Exception, InvalidEmailException {
    	client.activateUser(EMAIL);
    	client.spendPoints(EMAIL, 1000);
    }


}