package com.forkexec.hub.ws.it;

import java.io.IOException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import com.forkexec.hub.ws.FoodInit;
import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;

import com.forkexec.hub.ws.cli.HubClient;

import org.junit.After;
import org.junit.BeforeClass;

/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static HubClient client;

	protected static String MENU_ID = "010000100000";
	protected static String ENTREE = "ovos";
	protected static String PLATE = "bolognesa";
	protected static String DESSERT = "gelado";
	protected static int PRICE = 1024;
	protected static int PREPARATION_TIME = 2048;
	protected static String DESCRIPTION_TEXT = "ovos";
	protected static int DEFAULT_MENU_QUANTITY = 420;

	protected static String EMPTY_STRING = "";
	public static final int DEFAULT_INITIAL_BALANCE = 100;

	protected String VALID_EMAIL = "testtest@test.com";
	protected String INVALID_EMAIL = "test_test@t";
	protected String VALID_FAKE_CC_NUMBER = "5105105105105100";
	protected String INVALID_FAKE_CC_NUMBER = "510510A105105100";

	protected FoodId foodTestId = new FoodId();

	protected int MONEY_TO_ADD = 10;
	protected int POINTS_TO_ADD = 1000;
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
			client = new HubClient(uddiURL, wsName);
		} else {
			client = new HubClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));

		/*protected MenuId createMenuId() {
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

		protected List<MenuInit> createInitList(){

			MenuInit mi = new MenuInit();
			Menu m = createMenu();
			mi.setMenu(m);
			mi.setQuantity(DEFAULT_MENU_QUANTITY);
			List<MenuInit> lm = new ArrayList<>();
			lm.add(mi);

			return lm;
		}*/

	}
	
		protected FoodId createFoodId(){
		FoodId foodId = new FoodId();
		foodId.setRestaurantId("T02_Restaurant1");
		foodId.setMenuId("Menu2");

		foodTestId = foodId;
		return foodId;
		}

		protected Food createFood(){
			Food food = new Food();

			food.setId(createFoodId());
			food.setEntree(ENTREE);
			food.setPlate(PLATE);
			food.setDessert(DESSERT);
			food.setPrice(PRICE);
			food.setPreparationTime(PREPARATION_TIME);

			return food;
		}

		protected FoodId createFoodId2(){
		FoodId foodId = new FoodId();
		foodId.setRestaurantId("T02_Restaurant1");
		foodId.setMenuId("Menu3");
		return foodId;
		}

		protected Food createFood2(){
			Food food = new Food();

			food.setId(createFoodId2());
			food.setEntree("salad");
			food.setPlate("ovos");
			food.setDessert("cheesecake");
			food.setPrice(300);
			food.setPreparationTime(3000);

			return food;
		}

		protected FoodInit createFoodInit(){
			FoodInit f = new FoodInit();
			f.setFood(createFood());
			f.setQuantity(30);

			return f;
		}

		protected FoodInit createFoodInit2(){
			FoodInit f = new FoodInit();
			f.setFood(createFood2());
			f.setQuantity(30);

			return f;
		}

		protected List<FoodInit> createFoodInitList(){
			List<FoodInit> l = new ArrayList<>();
			l.add(createFoodInit());
			l.add(createFoodInit2());
			
			return l;
		}

	@After
	public void cleanup() {
		client.ctrlClear();
	}

}
