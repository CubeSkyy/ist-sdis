package com.forkexec.hub.ws.it;


import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class F1Scenario extends BaseIT {

    @Test
    public void success() throws InvalidUserIdFault_Exception {

        client.ctrlInitUserPoints(DEFAULT_INITIAL_BALANCE);
        System.out.println("Definir o default inicial balance para:" + DEFAULT_INITIAL_BALANCE);

        int amount = client.accountBalance(VALID_EMAIL);
        System.out.println("Criar conta de email com default balance:" + VALID_EMAIL);

        Assert.assertEquals(amount, DEFAULT_INITIAL_BALANCE);
        System.out.println("Conta criada com um balanaço de:" + amount);


        int add = 50;
        client.loadAccount(VALID_EMAIL, add, VALID_FAKE_CC_NUMBER);
        System.out.println("É feito um pedido de carregar a conta com:" + add);

        amount = client.accountBalance(VALID_EMAIL);
        Assert.assertEquals(amount, DEFAULT_INITIAL_BALANCE + add);
        System.out.println("Novo saldo da conta:" + amount);

    }
}
