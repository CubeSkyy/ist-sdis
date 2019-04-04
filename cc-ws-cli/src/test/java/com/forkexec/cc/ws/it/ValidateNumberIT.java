package com.forkexec.cc.ws.it;

import org.junit.Assert;
import org.junit.Test;

public class ValidateNumberIT extends BaseIT {
    private final String VALID_FAKE_CC_NUMBER = "4012888888881881";
    private final String INVALID_FAKE_CC_NUMBER = "2016872283588891";
    @Test
    public void success() {
        Assert.assertTrue(client.validateNumber(VALID_FAKE_CC_NUMBER));
    }

    @Test
    public void successInvalid() {
        Assert.assertFalse(client.validateNumber(INVALID_FAKE_CC_NUMBER));
    }

    @Test
    public void successEmpty() {
        Assert.assertFalse(client.validateNumber(""));
    }
    @Test
    public void successNull() {
        Assert.assertFalse(client.validateNumber(null));
    }
}
