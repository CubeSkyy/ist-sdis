package com.forkexec.cc.ws.it;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PingIT extends BaseIT {

    @Test
    public void success() {
        String name = "Cliente";
        Assert.assertEquals(client.ping(name), "Hello " + name + "!");
    }

    @Test
    public void successEmpty(){
        String name = "";
        Assert.assertEquals(client.ping(name), "Hello friend!");
    }

    @Test
    public void successNull(){
        String name = null;
        Assert.assertEquals(client.ping(name), "Hello friend!");
    }
}
