package com.forkexec.cc.ws.it;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class pingIT extends BaseIT {
    @Test
    public void success() {
        try {
            String a = client.ping("Ola");
            String b = "ola";

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
