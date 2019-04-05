package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class BadOrderException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadOrderException() {
	}

	public BadOrderException(String message) {
		super(message);
	}

}
