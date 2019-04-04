package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class NoCartForUser extends Exception {

	private static final long serialVersionUID = 1L;

	public NoCartForUser() {
	}

	public NoCartForUser(String message) {
		super(message);
	}

}