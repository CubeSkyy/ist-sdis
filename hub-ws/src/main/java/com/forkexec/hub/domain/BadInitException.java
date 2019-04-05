package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class BadInitException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadInitException() {
	}

	public BadInitException(String message) {
		super(message);
	}

}