package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class InvalidPointsException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPointsException() {
	}

	public InvalidPointsException(String message) {
		super(message);
	}

}