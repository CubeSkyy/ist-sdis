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
            List<MenuInit> lm = createInitList();
            client.ctrlInit(lm);

            Menu m = client.searchMenus(DESSERT).get(0);

            Assert.assertEquals(m.getId().getId(), MENU_ID);
            Assert.assertEquals(m.getEntree(), ENTREE);
            Assert.assertEquals(m.getPlate(), PLATE);
            Assert.assertEquals(m.getDessert(), DESSERT);
            Assert.assertEquals(m.getPrice(), PRICE);
            Assert.assertEquals(m.getPreparationTime(), PREPARATION_TIME);

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