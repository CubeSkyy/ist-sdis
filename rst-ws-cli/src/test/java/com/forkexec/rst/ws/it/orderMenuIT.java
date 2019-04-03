package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;

public class orderMenuIT extends BaseIT {

    private final int VALID_QUANTITY = 10;
    final String EXPECTED_ID = "PedidoNr1";
    final String EXPECTED_ID2 = "PedidoNr2";

    @Test
    public void success() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {

        final String EXPECTED_ID = "PedidoNr1";
        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        MenuOrder menuOrder = client.orderMenu(createMenuId(), VALID_QUANTITY);

        Assert.assertEquals(menuOrder.getId().getId(), EXPECTED_ID);

    }

    @Test
    public void success2Orders() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {

        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        MenuOrder menuOrder1 = client.orderMenu(createMenuId(), VALID_QUANTITY);
        MenuOrder menuOrder2 = client.orderMenu(createMenuId(), VALID_QUANTITY);
        Assert.assertEquals(menuOrder1.getId().getId(), EXPECTED_ID);
        Assert.assertEquals(menuOrder2.getId().getId(), EXPECTED_ID2);

    }

    @Test(expected = BadMenuIdFault_Exception.class)
    public void wrongMenuId() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        client.orderMenu(createMenuId("Test"), VALID_QUANTITY);
    }

    @Test(expected = BadQuantityFault_Exception.class)
    public void wrongQuantity() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        final int INVALID_QUANTITY = -10;
        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        client.orderMenu(createMenuId(), INVALID_QUANTITY);
    }

    @Test
    public void maxQuantity() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        MenuOrder menuOrder1 = client.orderMenu(createMenuId(), DEFAULT_MENU_QUANTITY);
        Assert.assertEquals(menuOrder1.getId().getId(), EXPECTED_ID);
    }

    @Test(expected = InsufficientQuantityFault_Exception.class)
    public void maxQuantity2Oders() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        MenuOrder menuOrder1 = client.orderMenu(createMenuId(), DEFAULT_MENU_QUANTITY/2);
        MenuOrder menuOrder2 = client.orderMenu(createMenuId(), DEFAULT_MENU_QUANTITY/2 + 1);
        Assert.assertEquals(menuOrder1.getId().getId(), EXPECTED_ID);
        Assert.assertEquals(menuOrder2.getId().getId(), EXPECTED_ID2);
    }

    @Test(expected = InsufficientQuantityFault_Exception.class)
    public void maxQuantityPlusOne() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        client.orderMenu(createMenuId(), DEFAULT_MENU_QUANTITY + 1);
    }

}
