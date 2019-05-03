package com.forkexec.hub.ws.it;


import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class F2Scenario extends BaseIT {


    @After
    public void cleanup() { //Overides BaseIT clear. Since one server is down when the test finishes, ctrlClear crashes the test before ending.
    }

    @Test
    public void success() throws InvalidUserIdFault_Exception, InvalidInitFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
        client.ctrlClear();
        client.ctrlInitUserPoints(DEFAULT_INITIAL_BALANCE);
        System.out.println("Definir o default inicial balance para: " + DEFAULT_INITIAL_BALANCE);
        System.out.println();

        client.activateAccount(VALID_EMAIL);
        System.out.println("Criada conta de email: " + VALID_EMAIL);
        System.out.println();

        int amount = client.accountBalance(VALID_EMAIL);
        Assert.assertEquals(amount, DEFAULT_INITIAL_BALANCE);
        System.out.println("Conta criada com um balanço de: " + amount);
        System.out.println();

        int add = 50;
        int pointsAdded = 5500;
        client.loadAccount(VALID_EMAIL, add, VALID_FAKE_CC_NUMBER);
        System.out.println("É feito um pedido de carregar a conta com: " + add + " euros (5500 pontos).");
        System.out.println();

        amount = client.accountBalance(VALID_EMAIL);
        Assert.assertEquals(amount, DEFAULT_INITIAL_BALANCE + pointsAdded);
        System.out.println("Novo saldo da conta: " + amount);
        System.out.println();

        System.out.println("Desligue um dos gestores de réplica do servidor Pontos");
        System.out.println();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        add = 30;
        pointsAdded= 3300;
        client.loadAccount(VALID_EMAIL, add, VALID_FAKE_CC_NUMBER);
        System.out.println("É feito um pedido de carregar a conta com: " + add + " euros (3300 pontos).");
        System.out.println();

        amount = client.accountBalance(VALID_EMAIL);
        Assert.assertEquals(amount, DEFAULT_INITIAL_BALANCE + 5500 + pointsAdded);
        System.out.println("Novo saldo da conta: " + amount);
        System.out.println();
        System.out.println("Exiting.");


    }
}
