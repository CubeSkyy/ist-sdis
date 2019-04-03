package com.forkexec.rst.ws.it;

import java.io.IOException;
import java.util.Properties;

import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.cli.RestaurantClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static RestaurantClient client;

	protected static String MENU_ID = "010000100000";
	protected static String ENTREE = "ovos";
	protected static String PLATE = "bolognesa";
	protected static String DESSERT = "gelado";
	protected static int PRICE = 1024;
	protected static int PREPARATION_TIME = 2048;

	protected static int DEFAULT_MENU_QUANTITY = 420;

	protected static String EMPTY_STRING = "";

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new RestaurantClient(uddiURL, wsName);
		} else {
			client = new RestaurantClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}
	protected MenuId createMenuId() {
		MenuId mi = new MenuId();
		mi.setId(MENU_ID);
		return mi;
	}

	protected Menu createMenu() {
		Menu m = new Menu();

		m.setId(createMenuId());
		m.setEntree(ENTREE);
		m.setPlate(PLATE);
		m.setDessert(DESSERT);
		m.setPrice(PRICE);
		m.setPreparationTime(PREPARATION_TIME);

		return m;
	}

	@AfterClass
	public static void cleanup() {
		client.ctrlClear();
	}

}
