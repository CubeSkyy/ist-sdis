package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class InvalidChargeException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidChargeException() {
	}

	public InvalidChargeException(String message) {
		super(message);
	}

}