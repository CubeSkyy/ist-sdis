package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class NotEnoughBalanceException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotEnoughBalanceException() {
	}

	public NotEnoughBalanceException(String message) {
		super(message);
	}

}
