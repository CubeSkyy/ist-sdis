package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class InvalidCCException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidCCException() {
	}

	public InvalidCCException(String message) {
		super(message);
	}

}