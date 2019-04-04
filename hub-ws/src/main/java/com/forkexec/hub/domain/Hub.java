package com.forkexec.hub.domain;

import javax.jws.WebService;

import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import java.util.Collection;


/**
 * Hub
 *
 * A restaurants hub server.
 *
 */
public class Hub {


	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Hub() {
		// Initialization of default values
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Hub INSTANCE = new Hub();
	}

	public static synchronized Hub getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void activateAccount(String userId) {

	}

    public PointsClient getPointsClient(){

	    PointsClient client = null;

	    try{
	        client = new PointsClient("http://t02:noRpzUdr@uddi.sd.rnl.tecnico.ulisboa.pt:9090","T02_Points1");
	     
	    } catch (PointsClientException e) {
	        System.out.println(e.getMessage());
	    } 

	    return client;
 	}

	public static void main(String[] args) throws Exception {
		PointsClient p = getPointsClient();
		System.out.println(p.ctrlPing());
	} 


	// TODO 
	
}
