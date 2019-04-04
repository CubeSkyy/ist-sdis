package com.forkexec.cc.ws.it;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class pingIT extends BaseIT {
    private String name = "Cliente";
    @Test
    public void success() {


        Assert.assertEquals(client.ping(name), "Hello " + name + "!");

    }
}
