package com.forkexec.rst.ws.it;
import com.forkexec.rst.ws.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class ctrlInitIT extends BaseIT {

    @Test
    public void success() throws BadInitFault_Exception, BadMenuIdFault_Exception {

        client.ctrlClear();

        MenuInit initMenu1 = new MenuInit();
        Menu m1 = createMenu();
        initMenu1.setMenu(m1);
        initMenu1.setQuantity(DEFAULT_MENU_QUANTITY);

        MenuInit initMenu2 = new MenuInit();
        Menu m2 = createMenu();
        initMenu2.setMenu(m2);
        initMenu2.setQuantity(DEFAULT_MENU_QUANTITY + 1);

        List<MenuInit> listMenu = new ArrayList<>();
        listMenu.add(initMenu1);
        listMenu.add(initMenu2);

        client.ctrlInit(listMenu);  

        Assert.assertEquals(m1.getId().getId(), client.getMenu(m1.getId()).getId().getId());
        Assert.assertEquals(m2.getId().getId(), client.getMenu(m2.getId()).getId().getId());        
    }

}
