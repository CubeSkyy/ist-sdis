package com.forkexec.hub.ws.it;


import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import org.junit.Assert;
import org.junit.Test;

public class F1Scenario extends BaseIT {

    @Test
    public void success() throws InvalidUserIdFault_Exception, InvalidInitFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {

        client.ctrlInitUserPoints(DEFAULT_INITIAL_BALANCE);
        System.out.println("Definir o default inicial balance para: " + DEFAULT_INITIAL_BALANCE);

        client.activateAccount(VALID_EMAIL);
        System.out.println("Criada conta de email: " + VALID_EMAIL);

        int amount = client.accountBalance(VALID_EMAIL);
        Assert.assertEquals(amount, DEFAULT_INITIAL_BALANCE);
        System.out.println("Conta criada com um balanço de: " + amount);


        int add = 50;
        client.loadAccount(VALID_EMAIL, add, VALID_FAKE_CC_NUMBER);
        System.out.println("É feito um pedido de carregar a conta com: " + add + " euros (5500 pontos).");
        int pointsAdded = 5500;
        amount = client.accountBalance(VALID_EMAIL);
        Assert.assertEquals(amount, DEFAULT_INITIAL_BALANCE + pointsAdded);
        System.out.println("Novo saldo da conta: " + amount);

        System.out.println("Exiting.");

    }
}
