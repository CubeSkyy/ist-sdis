package com.forkexec.hub.domain;

/** Exception used to signal a problem with the product quantity. */
public class BadTextException extends Exception {

    private static final long serialVersionUID = 1L;

    public BadTextException() {
    }

    public BadTextException(String message) {
        super(message);
    }

}
