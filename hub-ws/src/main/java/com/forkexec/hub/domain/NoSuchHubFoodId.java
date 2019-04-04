package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class NoSuchHubFoodId extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchHubFoodId() {
	}

	public NoSuchHubFoodId(String message) {
		super(message);
	}

}