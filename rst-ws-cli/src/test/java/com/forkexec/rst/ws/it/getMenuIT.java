

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
                m = client.getMenu(createMenuId());

                Assert.assertEquals(m.getId().getId(), MENU_ID);
                Assert.assertEquals(m.getEntree(), ENTREE);
                Assert.assertEquals(m.getPlate(), PLATE);
                Assert.assertEquals(m.getDessert(), DESSERT);
                Assert.assertEquals(m.getPrice(), PRICE);
                Assert.assertEquals(m.getPreparationTime(), PREPARATION_TIME);
	}

}
