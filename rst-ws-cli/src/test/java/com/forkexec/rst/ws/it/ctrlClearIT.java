package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;
import org.junit.Test;

import java.util.List;

public class ctrlClearIT extends BaseIT {
    final int QUANTITY_TO_ORDER = 10;

    @Test(expected = BadMenuIdFault_Exception.class)
    public void successMenu() throws BadMenuIdFault_Exception, BadInitFault_Exception {

        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        client.ctrlClear();
        client.getMenu(createMenuId());

    }

    @Test(expected = BadMenuIdFault_Exception.class)
    public void successOrder() throws BadMenuIdFault_Exception, BadInitFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {

        List<MenuInit> lm = createInitList();
        client.ctrlInit(lm);
        client.orderMenu(createMenuId(), QUANTITY_TO_ORDER);
        client.ctrlClear();
        client.getMenu(createMenuId());

    }


}
