package com.forkexec.pts.ws.it;

import java.io.IOException;
import java.util.Properties;

import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.FrontEndPoints;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;

/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

//	protected static PointsClient client;
	protected static FrontEndPoints client;
	public static final int DEFAULT_INITIAL_BALANCE = 100;
	protected static final String EMAIL = "test.email@test.com";
	protected static final String INVALID_EMAIL = "test.email.test.com";
	protected static final int INVALID_BALANCE = -33;	


	protected  static final String EMPTY_STRING = "";

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file %s", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		client = FrontEndPoints.getInstance();


//		final String uddiURL = testProps.getProperty("uddi.url");
//		final String wsName = testProps.getProperty("ws.name");
//		final String wsURL = testProps.getProperty("ws.url");

//		if ("true".equalsIgnoreCase(uddiEnabled)) {
//			client = new PointsClient(uddiURL, wsName);
//		} else {
//			client = new PointsClient(wsURL);
//		}
//		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}

	@After
	public void eachCleanup() {
		client.ctrlClear();
	}

}
