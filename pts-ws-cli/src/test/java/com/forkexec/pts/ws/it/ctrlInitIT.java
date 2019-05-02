package com.forkexec.pts.ws.it;


import com.forkexec.hub.domain.InvalidEmailException;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class ctrlInitIT extends BaseIT {

	//Nao da para comparar o pointBalance com o InitialBalance, so I guess just this will do?

    @Test
    public void success() throws BadInitFault_Exception, EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception, InvalidEmailException {

    	client.ctrlInit(DEFAULT_INITIAL_BALANCE); 
    	client.activateUser(EMAIL);
    	Assert.assertEquals(client.pointsBalance(EMAIL), DEFAULT_INITIAL_BALANCE);
   	
    }

     @Test(expected = BadInitFault_Exception.class)
     public void ctrlInitIT_Negative() throws BadInitFault_Exception {
     	client.ctrlInit(INVALID_BALANCE);
     }

}
