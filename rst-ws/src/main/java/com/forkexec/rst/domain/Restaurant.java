package com.forkexec.rst.domain;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Restaurant
 *
 * A restaurant server.
 *
 */
public class Restaurant {


	// Singleton -------------------------------------------------------------

	private Map<RestaurantMenuId, RestaurantMenu> menuMap = new ConcurrentHashMap<>();


	/** Private constructor prevents instantiation from other classes. */
	private Restaurant() {
		// Initialization of default values
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Restaurant INSTANCE = new Restaurant();
	}

	public static synchronized Restaurant getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void clearMenus(){ menuMap.clear(); }

	public void addMenu(RestaurantMenu m){
		menuMap.put(m.getId(),m);
	}

	public RestaurantMenu getMenu(RestaurantMenuId mid){
		return menuMap.get(mid);
	}
	// TODO 
	
}
