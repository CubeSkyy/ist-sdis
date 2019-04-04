package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class InvalidEmailException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidEmailException() {
	}

	public InvalidEmailException(String message) {
		super(message);
	}

}