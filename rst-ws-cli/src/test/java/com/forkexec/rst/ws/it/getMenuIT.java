package com.forkexec.rst.ws.it;

import com.forkexec.rst.ws.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class getMenuIT extends BaseIT {

	@Test
	public void success() throws BadMenuIdFault_Exception, BadInitFault_Exception{

                MenuInit menu_init = new MenuInit();
                Menu m = createMenu();
                menu_init.setMenu(m);
                menu_init.setQuantity(DEFAULT_MENU_QUANTITY);
                List<MenuInit> lmi = new ArrayList<>();
                lmi.add(menu_init);
                client.ctrlInit(lmi);

                Assert.assertEquals(m.getId().getId(), client.getMenu(m.getId()).getId().getId());

	}

}
