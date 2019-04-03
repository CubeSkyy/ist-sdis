package com.forkexec.pts.ws.it;


import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.BadInitFault_Exception;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ctrlInitIT extends BaseIT {

    @Test
    public void success() throws BadInitFault_Exception{
        client.ctrlInit(START_POINTS); 
        
        //Nao sei o que fazer mais aqui ._.
    }

}
