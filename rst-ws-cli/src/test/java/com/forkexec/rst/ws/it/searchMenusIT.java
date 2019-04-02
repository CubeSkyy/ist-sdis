package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class searchMenusIT extends BaseIT {

    private static String DESCRIPTIVE_TEXT = MENU_ID;


    @Test
    public void success() {
        try {
            MenuInit mi = new MenuInit();
            Menu m = createMenu();
            mi.setMenu(m);
            mi.setQuantity(DEFAULT_MENU_QUANTITY);
            List<MenuInit> lm = new ArrayList<>();
            lm.add(mi);
            client.ctrlInit(lm);

            //Cancro de codigo -_-
            Assert.assertEquals(client.searchMenus(DESCRIPTIVE_TEXT).get(0).getId().getId(), m.getId().getId());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = BadTextFault_Exception.class)
    public void BadTextFault_null() throws BadTextFault_Exception {
        client.searchMenus(null);

    }

    @Test(expected = BadTextFault_Exception.class)
    public void BadTextFault_emptyString() throws BadTextFault_Exception {
        client.searchMenus(EMPTY_STRING);

    }
}